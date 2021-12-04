package com.milan.tipster.service.impl;

import com.milan.tipster.dao.FaultRepository;
import com.milan.tipster.dao.GameCustomeRepository;
import com.milan.tipster.dao.GameRepository;
import com.milan.tipster.error.exception.TipsterException;
import com.milan.tipster.model.*;
import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.ESeason;
import com.milan.tipster.service.*;
import com.milan.tipster.util.Constants;
import com.milan.tipster.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.milan.tipster.model.enums.EFetchStatus.makeFetchStatus;
import static com.milan.tipster.util.Constants.MINUTES_REGEX_PATTERN;
import static com.milan.tipster.util.Utils.*;

@Slf4j
@Component
public class GameServiceImpl implements GameService {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameCustomeRepository gameCustomeRepository;

    @Autowired
    private FaultRepository faultRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private SeasonService seasonService;

    @Override
    public Game saveIfNotExist(Game game) {
        Objects.requireNonNull(game);
        Objects.requireNonNull(game.getCode());

        if (!gameRepository.existsByCode(game.getCode())) {
            game = gameRepository.save(game);
            log.info("Game {} saved to DB", game);
        } else {
            log.info("Game {} already exists in DB", game);
        }
        return game;
    }

    @Override
    public List<Game> findGamesWithoutDateInLink() {
        return gameRepository.findAllByPlayedOnIsNull();
    }

    @Override
    public List<Game> fetchGames(Document analysisDoc) {

        if (analysisDoc != null) {
            List<Game> games = new ArrayList<>();

            Element latest_analysis_page = analysisDoc.select("div.latest-analysis-page").first();
            Element tbody = latest_analysis_page.select("tbody").first();
            Elements tr = tbody.select("tr");
            for (Element gameTr : tr) {
                try {
                    Elements tds = gameTr.select("td");
                    Element gameEl = tds.get(1).select("a").first();
                    String[] parts = gameEl.text().split(" - ");
                    String gameLink = gameEl.attr("abs:href");
                    String gameCode = gameLink.replaceFirst("https://www.matchmoney.com.gr/match/", "")
                            .replace("/", "");
                    String gameNameGr = tds.get(1).text().trim();
                    String[] gameParts = null;

                    try {
                        String gameInEnglish = makeGameInEnglish(gameCode);
                        gameParts = gameInEnglish.split("-");
//
                    } catch (Exception e) {
                        log.error("GameInEngllish error {} {}", gameCode, e.getMessage());
                    }
//                    String dateStr = gameCode.split(gameInEnglish + "-")[1].replace("/", "-00");
                    LocalDateTime gamePlayedOn = null;
                    try {
                        gamePlayedOn = makeDatePlayedOn(gameCode);
                    } catch (Exception e) {
                        log.error("GamePlayedOn error {} {}", gameCode, e.getMessage());
                    }
//                     <td> 13:03 26/10/18</td>
                    LocalDateTime tipPublishedOn = makePublishedOn(tds.get(3).text());

                    if (gameRepository.existsByCode(gameCode)) {
                        log.info("----GAME ALREADY EXISTS---- gameCode:{}", gameCode);
                        continue;
                    }

                    // COUNTRY
                    Country country = countryService.findOrMakeCountry(tds, gameCode);
                    log.info("-----COUNTRY------{}", country);

                    // COMPETITION
                    Element flagEl = tds.get(0);
                    Element competitionEl = tds.get(2);
                    Competition competition = competitionService.findOrMakeCompetition(competitionEl, gameCode, country, gamePlayedOn, flagEl, tipPublishedOn);

                    Team homeTeam = null;
                    Team awayTeam = null;
                    if (parts.length > 1) {
                        /// HOME TEAM
                        String homeSideNameGr = parts[0].trim();
                        homeTeam = teamService.findOrMakeTeam(homeSideNameGr, gameParts, 0, gameCode, competition);
                        competition = competitionService.addTeamToCompetitionIfNotExists(competition, homeTeam);

                        // AWAY TEAM
                        String avawyTeamNameGr = parts[1].trim();
                        awayTeam = teamService.findOrMakeTeam(avawyTeamNameGr, gameParts, homeSideNameGr.split(" ").length, gameCode, competition);
                        competition = competitionService.addTeamToCompetitionIfNotExists(competition, awayTeam);
                    }

                    // GAME
                    Game game = makeAndSaveGame(gameCode, gameLink, competition, homeTeam, awayTeam, gamePlayedOn, tipPublishedOn, gameNameGr);

                    log.info("---- GAME CREATED---- {}", game);
                    games.add(game);
                } catch (Exception e) {
                    log.error("Game parsing error {} - ", gameTr.toString(), e);
                    faultRepository.save(Fault.builder().message(limitTextTo253(e.toString())).url(limitTextTo253(gameTr.toString())).build());
                }
            }
            return games;
        }
        return null;
        // Сделать update настроичного файла application-competitions-and-teams.yml (добавить code)
        // В конце может стоит подумать на счеть следущего сезона как маштабировать
    }

    private LocalDateTime makePublishedOn(String tipPublishedOnText) {
        String[] publishedParts = tipPublishedOnText.split(" ");
        return convertStringToLocalDateTime(publishedParts[1]
                + "-"
                + publishedParts[0]
                + ":00", "dd/MM/yy-HH:mm:ss"
        );
    }

    private LocalDateTime makeDatePlayedOn(String gameCode) {
        String dateStrFormat = "%s-%s-%s-%s-%s-%s";
        String[] gameCodeParts = gameCode.split("-");
        int size = gameCodeParts.length;
        while (!gameCodeParts[size - 1].matches(MINUTES_REGEX_PATTERN)) {
            size--;
        }
        String day = gameCodeParts[size - 5];
        String month = gameCodeParts[size - 4];
        String year = gameCodeParts[size - 3];
        String hour = gameCodeParts[size - 2];
        String minute = gameCodeParts[size - 1];
        String seconds = "00";

        LocalDateTime playedOn = convertStringToLocalDateTime(String.format(dateStrFormat, day, month, year, hour,
                minute, seconds), null);
        // add 1 hour for Moscow - Greece time difference
        return playedOn.plusHours(1L);
    }


    @Override
    public List<Game> findGamesOnDate(LocalDate localDate) {
        Objects.requireNonNull(localDate, "LocalDate shouldn't be null!");
        LocalDateTime start = localDate.atStartOfDay();
        LocalDateTime end = localDate.plusDays(1).atStartOfDay().minusMinutes(1);
        return gameRepository.findByPlayedOnBetween(start, end);
    }

    @Override
    public List<Game> findGamesByFetchStatus(EFetchStatus status) {
        return gameRepository.findAllByFetchStatus(status);
    }

    private static String makeGameInEnglish(String gameCode) {
        StringBuilder gameInEnglishSB = new StringBuilder();
        String[] gameCodeParts = gameCode.split("-");
        int size = gameCodeParts.length;
        while (!gameCodeParts[size - 1].matches(MINUTES_REGEX_PATTERN)) {
            size--;
        }
        size = size - 5;

        for (int i = 0; i < size; i++) {
            gameInEnglishSB.append(gameCodeParts[i]).append("-");
        }
        String gameInEnglish = gameInEnglishSB.toString();
        return trimLastChar(gameInEnglish);
    }


    private Game makeAndSaveGame(String gameCode,
                                 String gameLink,
                                 Competition competition,
                                 Team homeTeam,
                                 Team awayTeam,
                                 LocalDateTime gamePlayedOn,
                                 LocalDateTime tipPublishedOn,
                                 String gameNameGr) {
        // Score is unknown, that is why we put null like last field
        EFetchStatus status = makeFetchStatus(gameCode,
                gameLink,
                competition,
                homeTeam,
                awayTeam,
                gamePlayedOn,
                tipPublishedOn,
                gameNameGr,
                null);

        Game game = Game.builder()
                .link(gameLink)
                .code(gameCode)
                .playedOn(gamePlayedOn)
                .competition(competition)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .published(tipPublishedOn)
                .nameGr(gameNameGr)
                .fetchStatus(status)
                .build();
        game = gameRepository.save(game);
        return game;
    }

    /**
     * Добавляеть дату проведения матча
     *
     * @param mathcDoc Страничка в matchMoney с матчем
     * @param game     - Матч
     */
    @Override
    public boolean updatePlayedOnLocalDateTime(Game game, Document mathcDoc) {
        Objects.requireNonNull(mathcDoc, "GameDoc shouldn't be null");
        Objects.requireNonNull(game, "Game shouldn't be null");
        /////////////////////// game playedOn date fetching ////////////////////
        LocalDateTime playedOnDate = null;
        Element dateAndTimeElement = null;
        try {
            dateAndTimeElement = mathcDoc.selectFirst("span.time-label");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yy");
            playedOnDate = LocalDateTime.parse(dateAndTimeElement.text(), formatter);

        } catch (Exception e) {
            log.warn("Error fetching playedOn date for game {} \ndateAndTimeElement: {} ", game.getCode(), dateAndTimeElement != null ? dateAndTimeElement.text() : null);
        }
        if (playedOnDate == null && game.getPublished() != null) {
            playedOnDate = game.getPublished().plusHours(12);
        }
        return gameCustomeRepository.updatePlayedOn(game.getGameId(), playedOnDate);
    }

    @Override
    public Game update(Game game) {
        game = updateFetchStatus(game, false);
        return gameRepository.save(game);
    }

    @Override
    public Game findGameByCode(String gameCode) {
        try {
            Game game = gameRepository.findByCode(gameCode);
            return game;
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("Dublicate db records found for game code {}", gameCode);
            throw e;
        }

    }

    @Override
    public Game fetchGameFromTipDoc(Document tipDoc, String gameCode) {
        Objects.requireNonNull(tipDoc, "TipDoc!");
        Objects.requireNonNull(gameCode, "Game code!");

        // playedOn
        LocalDateTime playedOn = null;
        LocalDateTime publishedOn = null;
        try {
            playedOn = makeDatePlayedOn(gameCode);

            // publishedOn
            Element rowAuthorEl = tipDoc.selectFirst("div.row-author");
            Element authorDivEl = rowAuthorEl.selectFirst("div");
            Element tipPublishedOnEl = authorDivEl.select("span").get(1);
            // <td> 13:03 26/10/18</td>
            // <span>Ημερομηνία: 11:24 20/08/19</span>

            publishedOn = makePublishedOn(tipPublishedOnEl.text()
                    .replace("Ημερομηνία: ", ""));
            if (playedOn == null) {
                // If we cant make it we set it to publishedOn + 12 hours by default
                playedOn = publishedOn.plusHours(12);
            }
        } catch (Exception e) {
            log.error("Error making playedOn or publishedOn for game " + gameCode);
        }

        // competition (code or nameGr)
        Element seasonLabelEl = tipDoc.selectFirst("div.season-label");
        if (seasonLabelEl == null) {
            throw new TipsterException("Season Label not found for game " + gameCode);
        }
        Elements seasonSpanEls = seasonLabelEl.select("span");
        Country country = countryService.findCountryFromSeasonLabel(seasonLabelEl);
        if (country == null) {
            throw new TipsterException("Country not found for game " + gameCode);
        }

        String[] seasonParts = seasonSpanEls.get(1).text().split("-");
        String competitionSubCat = Utils.greekToUpper(seasonParts[0].trim());
        ESeason season = seasonService.findSeasonByYear(seasonParts[1].trim());
        // Не находит competition
        Competition competition = competitionService.findCompetition(country, competitionSubCat, season);
        if (competition == null) {
            competition = competitionService.makeNewCompetition(gameCode, country, competitionSubCat, season, null);
//            throw new TipsterException("Competition not found for game " + gameCode);
        }


        // home team (code or nameGr)
        Element homeTeamEl = seasonLabelEl.siblingElements().select("div.home").first();
        String homeTeamGr = homeTeamEl.selectFirst("h1.team-name").text().trim();
        Team homeTeam = teamService.findTeamInCompetitionByTeamNameGr(competition, homeTeamGr);

        // away team (code or nameGr)
        Element awayTeamEl = seasonLabelEl.siblingElements().select("div.away").first();
        String awayTeamGr = awayTeamEl.selectFirst("h1.team-name").text().trim();
        Team awayTeam = teamService.findTeamInCompetitionByTeamNameGr(competition, awayTeamGr);

        // GAME
        String gameNameGr = homeTeamGr + " - " + awayTeamGr;
        String gameLink = Constants.URL_MATCH_MONEY + gameCode + "/";
        Game game = makeAndSaveGame(gameCode, gameLink, competition, homeTeam, awayTeam, playedOn, publishedOn, gameNameGr);

        log.info("---- GAME CREATED---- {}", game);
        return game;

    }

    @Override
    public boolean updateNameGrFromTeams(Game game) {
        Objects.requireNonNull(game, "Game!");
        Objects.requireNonNull(game.getHomeTeam(), "Home team!");
        Objects.requireNonNull(game.getAwayTeam(), "Away team!");

        String gameNameGr = game.getHomeTeam().getNameGr() + " - " + game.getAwayTeam().getNameGr();
        return gameCustomeRepository.updateNameGr(game.getGameId(), gameNameGr);
    }

    /**
     * Critiria for Fully_Fetched - not empty: code, link, competition, nameGr, playedOn, homeTeam, awayTeam, published, score
     *
     * @param game
     * @return
     */
    @Override
    public Game updateFetchStatus(Game game, boolean needBDUpdate) {
        Objects.requireNonNull(game, "Game must not be empty");
        EFetchStatus status = makeFetchStatus(game.getCode(),
                game.getLink(),
                game.getCompetition(),
                game.getNameGr(),
                game.getPlayedOn(),
                game.getHomeTeam(),
                game.getAwayTeam(),
                game.getPublished(),
                game.getScore()
        );
        game.setFetchStatus(status);
        if (needBDUpdate) {
            gameCustomeRepository.updateFetchStatus(game.getGameId(), status);
        }
        return game;
    }

    @Override
    public boolean updateScore(Game game) {
        return gameCustomeRepository.updateScore(game.getGameId(), game.getScore());
    }

    @Override
    public List<Game> findGamesByPick(EPick unknown) {
        return gameCustomeRepository.findGamesByPick(unknown);
    }

}
