package com.milan.tipster.service.impl;

import com.milan.tipster.dao.CompetitionRepository;
import com.milan.tipster.dao.TipRepository;
import com.milan.tipster.error.exception.TipsterException;
import com.milan.tipster.model.*;
import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.ESeason;
import com.milan.tipster.service.CompetitionService;
import com.milan.tipster.service.SeasonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.milan.tipster.util.Utils.greekToUpper;

@Slf4j
@Component
public class CompetitionServiceImpl implements CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private SeasonService seasonService;

    @Autowired
    private TipRepository tipRepository;

    @Override
    public Competition findOrMakeCompetition(Element competitionEl,
                                             String gameCode,
                                             Country country,
                                             LocalDateTime gamePlayedOn,
                                             Element flagEl,
                                             LocalDateTime tipPublishedOn) {
        String compNameGr1 = greekToUpper(competitionEl.text().trim());
        String compNameGr2 = compNameGr1.split(" - ")[1];
        ESeason season = seasonService.determineSeason(gamePlayedOn != null ? gamePlayedOn : tipPublishedOn);
        String[] flagParts = flagEl.text().split(" ");
        String compLabel = flagParts[flagParts.length - 1];
        Competition competition = findBySeasonAndNameGrOrNameGrAllCase(compNameGr1, compNameGr2, season);
        if (competition == null) {
            competition = makeNewCompetition(gameCode, country, compNameGr1, season, compLabel);
        } else if (EFetchStatus.needsFetching(competition.getFetchStatus())) {
            competition.setNameGr(compNameGr1);
            competition.setCountry(country);
            competition.setSeason(season);
            competition.setLabel(compLabel);
            competition = updateFetchStatus(competition);
            competition = competitionRepository.save(competition);
            log.info("------COMPETITION UPDATED TO DB-------{}", competition.getNameGr());
        }
        return competition;
    }

    @Override
    public Competition makeNewCompetition(String gameCode, Country country, String compNameGr1, ESeason season, String compLabel) {
        Competition competition;
        log.warn("----WARN----COMPETITION NOT FOUND----{}---gameCode:{}---season:{}", compNameGr1, gameCode, season);
        competition = Competition.builder()
                .nameGr(compNameGr1)
                .country(country)
                .season(season)
                .label(compLabel)
                .build();
        competition = updateFetchStatus(competition);
        competition = competitionRepository.save(competition);
        log.info("------NEW COMPETITION SAVED TO DB-------{}", competition.getNameGr());
        return competition;
    }

    @Override
    public Competition findBySeasonAndNameGrOrNameGrAllCase(String nameGr1, String nameGr2, ESeason season) {
        Objects.requireNonNull(nameGr1);
        Objects.requireNonNull(nameGr2);

        Competition competition = competitionRepository.findByNameGrAndSeason(nameGr1, season);
        if (competition == null) {
            competition = competitionRepository.findByNameGrAndSeason(nameGr1.toUpperCase(), season);
            if (competition == null) {
                competition = competitionRepository.findByNameGrAndSeason(nameGr2, season);
                if (competition == null) {
                    competition = competitionRepository.findByNameGrAndSeason(nameGr2.toUpperCase(), season);
                    if (competition == null) {
                        competition = competitionRepository.findByNameGrAndSeason(greekToUpper(nameGr1), season);
                        if (competition == null) {
                            competition = competitionRepository.findByNameGrAndSeason(greekToUpper(nameGr2), season);
                            if (competition == null ) {
                                log.error("Competition with nameGr1 {} or nameGr2 {} and season {} not found in DB!", nameGr1, nameGr2, season);
                                throw new TipsterException("Competition not found in DB!");
                            }
                        }
                    }
                }
            }
        }
        return competition;
    }

    @Override
    public Competition findByNameGrNoMetterCase(String nameGr, ESeason season) {
        Objects.requireNonNull(nameGr);
        Competition competition = competitionRepository.findByNameGrAndSeason(nameGr, season);
        if (competition == null) {
            competition = competitionRepository.findByNameGrAndSeason(nameGr.toUpperCase(), season);
            if (competition == null) {
                competition = competitionRepository.findByNameGrAndSeason(greekToUpper(nameGr), season);
            }
        }
        return competition;
    }

    @Override
    public Competition save(Competition competition) {
        return competitionRepository.save(competition);
    }

    @Override
    public Competition addTeamToCompetitionIfNotExists(Competition competition, Team team) {
        Objects.requireNonNull(competition, "Competition could not be empty");
        if (competition.getTeams() != null) {
            for (Team compTeam : competition.getTeams()) {
                if (team.getNameGr().equalsIgnoreCase(compTeam.getNameGr())) {
                    return competition;
                }
            }
        } else {
            competition.setTeams(new HashSet<>());
        }
        competition.getTeams().add(team);
        return competitionRepository.save(competition);
    }

    // TODO maybe dont need this use repository method straigthforward
    @Override
    public List<Competition> findAllTipmansCompetitions(Tipman tipman) {
        Objects.requireNonNull(tipman, "Tipman");

        return competitionRepository.tipmansCompetitions(tipman.getFullName());
    }

    @Override
    public List<Competition> findAllCompetitionsByCountry(Country country) {
        return competitionRepository.findAllByCountry(country);
    }

    @Override
    public Competition findCompetition(Country country, String competitionSubCat, ESeason season) {
        List<Competition> competitions = competitionRepository.findAllByCountryAndSeason(country, season);
        if (competitions != null) {
            for (Competition competition : competitions) {
                String compCode = competition.getCode();
                if (Objects.nonNull(compCode) && compCode.contains(competitionSubCat)) {
                    return competition;
                }
                String compNameGr = competition.getNameGr();
                if (Objects.nonNull(compNameGr) && compNameGr.contains(competitionSubCat)) {
                    return competition;
                }
            }
        }
        return null;
    }


    public Competition updateFetchStatus(Competition competition) {
        Objects.requireNonNull(competition, "Competition must not be empty");
        boolean notFetched = false;
        boolean fetched = false;
        if (StringUtils.isBlank(competition.getNameGr())) {
            notFetched = true;
        } else {
            fetched = true;
        }
        if (StringUtils.isBlank(competition.getLabel())) {
            notFetched = true;
        } else {
            fetched = true;
        }
        if (competition.getSeason() == null) {
            notFetched = true;
        } else {
            fetched = true;
        }
        if (competition.getCountry() == null) {
            notFetched = true;
        } else {
            fetched = true;
        }

        if (fetched && notFetched) {
            competition.setFetchStatus(EFetchStatus.PARTLY_FETCHED);
        } else if (fetched) {
            competition.setFetchStatus(EFetchStatus.FULLY_FETCHED);
        } else {
            competition.setFetchStatus(EFetchStatus.NOT_FETCHED);
        }
        return competition;
    }
}
