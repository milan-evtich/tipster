package com.milan.tipster.service;

import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Country;
import com.milan.tipster.model.Team;
import com.milan.tipster.model.Tipman;
import com.milan.tipster.model.enums.ESeason;
import org.jsoup.nodes.Element;

import java.time.LocalDateTime;
import java.util.List;

public interface CompetitionService {

    /**
     * Парсит nameGr соревнования из competitionEl,
     * ищет в БД по nameGr(просто, upper, greekUpper),
     * если не находит создает такое соревнование
     * @param competitionEl
     * @param gameCode
     * @param flagEl
     * @param tipPublishedOn
     * @return соревнование
     */
    Competition findOrMakeCompetition(Element competitionEl, String gameCode, Country country, LocalDateTime gamePlayedOn, Element flagEl, LocalDateTime tipPublishedOn);

    Competition findBySeasonAndNameGrOrNameGrAllCase(String nameGr1, String nameGr2, ESeason season);

    Competition findByNameGrNoMetterCase(String nameGr, ESeason season);

    Competition save(Competition competition);

    Competition addTeamToCompetitionIfNotExists(Competition competition, Team team);

    List<Competition> findAllTipmansCompetitions(Tipman tipman);

    List<Competition> findAllCompetitionsByCountry(Country country);

    Competition findCompetition(Country country, String competitionSubCat, ESeason season);
}
