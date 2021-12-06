package com.milan.tipster.service.impl;

import com.google.common.collect.ImmutableMap;
import com.milan.tipster.dao.BookieRepository;
import com.milan.tipster.dao.FaultRepository;
import com.milan.tipster.dao.TipCustomRepository;
import com.milan.tipster.dao.TipRepository;
import com.milan.tipster.dao.TipmanCompetitionRatingRepository;
import com.milan.tipster.dto.PredictionTipDto;
import com.milan.tipster.dto.TipmanCompetitionDto;
import com.milan.tipster.error.exception.GameException;
import com.milan.tipster.error.exception.TipAlreadyExistsWarning;
import com.milan.tipster.error.exception.TipsterException;
import com.milan.tipster.mapper.TipToPredictionOrikaMapper;
import com.milan.tipster.model.*;
import com.milan.tipster.model.enums.*;
import com.milan.tipster.service.*;
import com.milan.tipster.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.milan.tipster.model.enums.EFetchStatus.makeFetchStatus;
import static com.milan.tipster.model.enums.ETipFilter.validTipsV1;
import static com.milan.tipster.model.enums.ETipFilter.validTipsV2;
import static com.milan.tipster.util.Constants.*;
import static com.milan.tipster.util.ScoreUtils.oddsScore;
import static com.milan.tipster.util.Utils.*;
import static com.milan.tipster.util.Utils.textHasAnyOfWords;
import static java.lang.Double.parseDouble;

@Slf4j
@Component
public class TipServiceImpl implements TipService {

    @Autowired
    private FetchingService fetchingService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private TipmanService tipmanService;
    @Autowired
    private TipRepository tipRepository;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private GameService gameService;
    @Autowired
    private FaultRepository faultRepository;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private TipCustomRepository tipCustomRepository;
    @Autowired
    private BookieRepository bookieRepository;
    @Autowired
    private TipmanCompetitionRatingRepository tipmanCompetitionRatingRepository;
    @Autowired
    private TipToPredictionOrikaMapper tipToPredictionOrikaMapper;

    static final Map<ETipFilter, Predicate<Tip>> tipsFilterMap = ImmutableMap.of(
            ETipFilter.DEFAULT, validTipsV1,
            ETipFilter.ODDS_1_9__2_5_TIPMAN_16_COMP_35, validTipsV1,
            ETipFilter.ODDS_1_5__3_9_TIPMAN_17_COMP_43, validTipsV2
    );

    @Override
    public int fetchTipsPlayedOnNull(boolean fetchFromFile) {
        List<Game> games = gameService.findGamesWithoutDateInLink();
        return fetchTipsForGames(games, fetchFromFile);
    }

    @Override
    public int fetchTipsWithGameFetchStatusPartlyFetched(boolean fetchFromFile) {
        List<Game> games = gameService.findGamesByFetchStatus(EFetchStatus.PARTLY_FETCHED);
        log.info("Start fetching {} games!", games.size());
        return fetchTipsForGames(games, fetchFromFile);
    }

    @Override
    public int fetchTipsOnDate(LocalDate datePlayedOn, boolean fetchFromFile) {
        List<Game> games = gameService.findGamesOnDate(datePlayedOn);
        return fetchTipsForGames(games, fetchFromFile);
    }

    @Override
    public int fetchTipsForGames(List<Game> games, boolean fetchFromFile) {
        Objects.requireNonNull(games);
        int count = 0;
        for (Game game : games) {
            try {
                boolean tipUpdatedOrSaved = fetchAndSaveOrUpdateTip(game, fetchFromFile);
                if (tipUpdatedOrSaved) {
                    log.info("NEW TIP SAVED OR UPDATED for game: {}", game.getCode());
                    count++;
                    game = gameService.findGameByCode(game.getCode());
                    gameService.updateFetchStatus(game, true);
                }
            } catch (TipAlreadyExistsWarning tipAlreadyExistsWarning) {
                log.warn("TIP ALREADY EXISTS AND FULLY FETCHED {}", game.getCode());
            } catch (Exception e) {
                log.error("Error fetching tip for game:{} - ", game.getLink(), e);
                faultRepository.save(Fault.builder().message(limitTextTo253(e.toString())).url(limitTextTo253(game.getCode())).build());
            }
        }
        return count;
    }

    @Override
    public boolean fetchAndSaveOrUpdateTip(Game game, boolean fetchFromFile) throws IOException {
        Objects.requireNonNull(game, "Game shouldn't be null");
        if (StringUtils.isEmpty(game.getLink())) {
            throw new TipsterException("Game link is empty for game: " + game.getLink());
        }

        String url = fetchFromFile ? DEFAULT_FILE_STORAGE_DIR + GAMES_DIR_NAME + "/" + game.getCode() + ".html" : game.getLink();
        Document tipDocument = fetchingService.fetchDocByUrlOrPath(url, fetchFromFile);
        /*Element body = tipDocument.body();
        Document simpleDoc = new Document(tipDocument.baseUri());
        DocumentType docType = new DocumentType("html", "", "", "");
        simpleDoc.appendChild(docType);
        Element html = simpleDoc.createElement("html");
        simpleDoc.appendChild(html);
        html.appendChild(simpleDoc.createElement("head"));
        html.appendChild(body);*/

        // SAVING FILE ON DISK
//        fileStorageService.saveToFileIfNotExists(tipDocument,
//                DEFAULT_FILE_STORAGE_DIR + GAMES_DIR_NAME + "/" + game.getCode(),
//                false);

        boolean tipUpdated = false;

        try {
            tipUpdated = updateOrSaveTip(game, tipDocument);
        } catch (TipAlreadyExistsWarning taew) {
            log.warn("Tip already exists ", taew);
        }
        if (game.getPlayedOn() == null) {
            gameService.updatePlayedOnLocalDateTime(game, tipDocument);
        }
        if (game.getNameGr() == null) {
            gameService.updateNameGrFromTeams(game);
        }
        return tipUpdated;
    }

    /**
     * Search for the game, create it if not exist, and make or update tip for the game
     *
     * @param tipDoc
     * @param fileName
     * @param absoluteFilePath
     * @return
     */
    @Override
    public Boolean updateTipFromFile(Document tipDoc, String fileName, String absoluteFilePath) {
        String gameCode = fileName.replace(".html", "");

        boolean needToChangeDir = false;
        if (gameCode.startsWith("games")) {
            needToChangeDir = true;
            gameCode = gameCode.replace("games", "");
        }
        Game game = gameService.findGameByCode(gameCode);
        if (game == null) {
            log.warn("Game not found for gameCode {} ", gameCode);
            try {
                game = gameService.fetchGameFromTipDoc(tipDoc, gameCode);
            } catch (TipsterException e) {
                log.error("Error in fetching game {} ", gameCode, e);
            }
            if (game == null) {
                fileStorageService.moveFileToDir(absoluteFilePath, ERROR_FILE_STORAGE_DIR);
                throw new GameException("Game couldn't be fetched for gameCode " + gameCode);
            }
        }
        Tipman tipman = tipmanService.fetchAndSaveTipman(tipDoc);
        if (tipman == null) {
            fileStorageService.moveFileToDir(absoluteFilePath, ERROR_FILE_STORAGE_DIR);
            throw new TipsterException("Tipman not found for game " + game.getCode());
        } else {
            Competition competition = game.getCompetition();
            if (Objects.nonNull(competition)) {
                // update tipman_competition
                Optional<Competition> competitionOptional = tipman
                        .getCompetitions()
                        .stream()
                        .filter(c -> c
                                .getCompetitionId()
                                .equals(competition.getCompetitionId()))
                        .findAny();
                if (!competitionOptional.isPresent()) {
                    tipmanService.makeTipmanCompetition(tipman.getTipmanId(), competition.getCompetitionId());
                }
            }
        }
        boolean tipUpdatedOrSaved = false;
        try {
            tipUpdatedOrSaved = updateOrSaveTip(game, tipDoc);
        } catch (TipAlreadyExistsWarning tipAlreadyExistsWarning) {
            log.warn("TIP ALREADY EXISTS AND FULLY FETCHED {}", game.getCode());
        }
        if (needToChangeDir) {
            fileStorageService.moveFileToDir(absoluteFilePath, DEFAULT_FILE_STORAGE_DIR + GAMES_DIR_NAME);
        }
        return tipUpdatedOrSaved;
    }

    private boolean updateOrSaveTip(Game game, Document tipDocument) {
        Tipman tipman = tipmanService.fetchAndSaveTipman(tipDocument);
        if (tipman == null) {
            throw new TipsterException("Tipman not found for game - " + game.getLink());
        }
        ratingService.createTipmanCompetitionRatingIfNotExist(tipman, game.getCompetition());
        Tip tip = tipRepository.findByGameAndTipman(game, tipman);
        boolean tipUpdatedOrSaved = false;
        if (tip != null) {
//            if (EFetchStatus.needsFetching(tip)) {
            tipUpdatedOrSaved = updateTip(tip, tipDocument, tipman, game);
            if (tipUpdatedOrSaved) {
                log.info("TIP UPDATED| game:{} | tip: status {}, pick {}", game.getLink(), tip.getStatus(), tip.getPick());
            }
//            } else {
//                throw new TipAlreadyExistsWarning(game.getLink());
//            }
        } else {
            createTip(tipDocument, tipman, game);
            tipUpdatedOrSaved = true;
        }

        return tipUpdatedOrSaved;
    }


    @Override
    public List<Tip> findAllUnratedTips() {
        return tipRepository
                .findAllByRatedFlagIsNullOrRatedFlag_TipmanRatedOrRatedFlag_CompetitionRatedOrRatedFlag_TcRated(false,
                        false, false);
    }

    @Override
    public int fetchTipsPickUnknown(boolean fileOrUrl) {
        List<Tip> tips = tipRepository.findAllByPick(EPick.UNKNOWN);

        int count = 0;
        for (Tip tip : tips) {
            Game game = tip.getGame();
            if (game != null) {
                try {
                    String urlOrPath = "";
                    if (fileOrUrl) {
                        urlOrPath = DEFAULT_FILE_STORAGE_DIR + GAMES_DIR_NAME + "/" + game.getCode() + ".html";
                    } else {
                        urlOrPath = game.getLink();
                    }
                    Document tipDoc = fetchingService.fetchDocByUrlOrPath(urlOrPath, fileOrUrl);
                    if (makeAndUpdatePick(tip, tipDoc)) {
                        log.info("Tip updated");
                        count++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return count;
    }

    @Override
    public int fetchTipsWithFetchStatusPartlyFetched(boolean fetchFromFile) {
        List<Tip> tips = tipRepository.findAllByFetchStatus(EFetchStatus.PARTLY_FETCHED);
        List<Game> games = tips.stream().map(Tip::getGame).collect(Collectors.toList());
        return fetchTipsForGames(games, fetchFromFile);
    }

    @Override
    public int fetchTipsWithOddsNull(boolean fetchFromFile) {
        List<Tip> tips = tipRepository.findAllByOdds(null);
        List<Game> games = tips.stream().map(Tip::getGame).collect(Collectors.toList());
        return fetchTipsForGames(games, fetchFromFile);
    }

    @Override
    @Transactional
    public List<PredictionTipDto> getTipsPredictionForToday(int top, Integer hours, Integer minutes, ETipFilter filter) {
        LocalDateTime startDateTime = LocalDateTime.now().plusHours(hours).plusMinutes(minutes);
        List<Tip> openTips = tipRepository.findAllByStatusAndGame_PlayedOnAfter(ETipStatus.OPEN, startDateTime);
//        Map<RankedTip> rankedTips = new ArrayList<>();
        // all tips that can be played
        return mapToPredictionDtos(openTips, tipsFilterMap.get(filter));
    }

    @Override
    public List<PredictionTipDto> getTipsPredictionForPeriod(int top, LocalDateTime start, LocalDateTime end) {
        List<Tip> openTips = tipRepository.findAllByStatusAndGame_PlayedOnBetween(ETipStatus.OPEN, start, end);
//        Map<RankedTip> rankedTips = new ArrayList<>();
        // all tips that can be played
        return mapToPredictionDtos(openTips, tipsFilterMap.get(ETipFilter.DEFAULT));
    }

    private List<PredictionTipDto> mapToPredictionDtos(List<Tip> openTips, Predicate<Tip> validTips) {
        return openTips
                .stream()
                .filter(tip -> Objects.nonNull(tip.getOdds()))
                .map(this::makePredictionScore)
                .filter(validTips)
                .map(tipToPredictionOrikaMapper::toDto)
                .map(this::addTipmanCompetitionRating)
                .sorted(Comparator
                        .comparingDouble(PredictionTipDto::getScore)
                        .reversed())
                .collect(Collectors.toList())
                ;
    }

    private PredictionTipDto addTipmanCompetitionRating(PredictionTipDto predictionTipDto) {
        TipmanCompetitionRating tipmanCompetitionRating = tipmanCompetitionRatingRepository.findTipmanCompetitionRating(predictionTipDto.getTipman().getTipmanId(),
                predictionTipDto.getGame().getCompetition().getCompetitionId());
        predictionTipDto.setTipmanCompetition(tipToPredictionOrikaMapper.map(tipmanCompetitionRating, TipmanCompetitionDto.class));
        return predictionTipDto;
    }

    @Override
    public int fetchTipsWithStatusAndPlayedAlready(boolean fetchFromFile, ETipStatus status) {
        List<Tip> openTips = tipRepository.findAllByStatus(ETipStatus.OPEN);
        List<Game> openGames = openTips
                .stream()
                .map(Tip::getGame)
                .filter(game -> game
                        .getPlayedOn()
                        .isBefore(LocalDateTime.now().minusHours(2L)))
                .collect(Collectors.toList());

        log.info("Start fetching {} games!", openGames.size());
        return fetchTipsForGames(openGames, fetchFromFile);
    }

    @Override
    public int fetchTipsWithStatusAndNotPlayedYet(boolean fetchFromFile, ETipStatus status) {
        List<Tip> openTips = tipRepository.findAllByStatus(ETipStatus.OPEN);
        List<Game> openGames = openTips
                .stream()
                .map(Tip::getGame)
                .filter(game -> game
                        .getPlayedOn()
                        .isAfter(LocalDateTime.now().plusMinutes(20L)))
                .collect(Collectors.toList());

        log.info("Start fetching {} games!", openGames.size());
        return fetchTipsForGames(openGames, fetchFromFile);
    }

    // Calculate overall score of the tip in order to determine the top best open tips for now
     private Tip makePredictionScore(Tip tip) {
        Competition competition = tip.getGame().getCompetition();
        Tipman tipman = tip.getTipman();
        TipmanCompetitionRating tipmanCompetitionRating = tipmanCompetitionRatingRepository.findTipmanCompetitionRating(tipman.getTipmanId(), competition.getCompetitionId());
        double tcOverallScore = 0D;
        if (Objects.nonNull(tipmanCompetitionRating)) {
            tcOverallScore = tipmanCompetitionRating.getRating().getOverallScore();
        }
        double score =
                9 * tcOverallScore
                + 8 * tipman.getRating().getOverallScore()
                + 7 * competition.getRating().getOverallScore()
                + 6 * tip.getPick().score()
                + 5 * oddsScore(tip.getOdds())
                + 10 * (tip.getHotMatch() ? 1 : 0)
                ;

        tip.setScore(score);
        return tipRepository.save(tip);
    }





    /**
     * Updates spot tip
     *
     * @param tip
     * @param tipDoc
     * @param tipman
     * @param game
     * @return
     */
    private boolean updateTip(Tip tip, Document tipDoc, Tipman tipman, Game game) {
        Objects.requireNonNull(tipman, "Tipman must be not empty!");
        Objects.requireNonNull(tipDoc, "TipDoc must be not empty!");
        Objects.requireNonNull(game, "Game must be not empty!");

        Element spotDiv = tipDoc.selectFirst("div[data-tabconnect=spot]");

        boolean updated = false;

        ///////////////// make score ////////////////////////
        updated = makeGameScore(tipDoc, game);
        if (updated) {
            game = gameService.findGameByCode(game.getCode());
            tip.setGame(game);

        }

        ///////////////// make pick ////////////////////////
        updated = updated || makeAndUpdatePick(tip, tipDoc);

        ///////////////// make odds ////////////////////////
        updated = updated || makeAndUpdateOdds(tip, tipDoc);

        ///////////////// make hot match flag ///////////////////
        updated = updated || makeAndUpdateHotMatch(tip, tipDoc);

        ///////////////// make status //////////////////////

        updated = updated || makeAndUpdateStatus(tip, game.getScore(), true);

        // type
        updated = updated || updateType(tip, ETipType.SPOT);

        // tipman
        updated = updated || updateTipman(tip, tipman);

        // game
        updated = updated || updateGame(tip, game);

        // text
        updated = updated || updateText(tip, spotDiv);

        // strongText
        updated = updated || updateStrongText(tip, spotDiv);

        // fetchStatus
        updated = updated || updateFetchStatus(tip, true);

        return updated;
    }

    private boolean updateStrongText(Tip tip, Element spotDiv) {
        String strongText = getStrongText(spotDiv, true);
        if (strongText != null && !strongText.equals(tip.getStrong())) {
            return tipCustomRepository.updateStrong(tip.getTipId(), strongText);
        }
        return false;
    }

    private boolean updateText(Tip tip, Element spotDiv) {
        String text = Utils.limitTextTo253(spotDiv.text());
        if (text != null && !text.equals(tip.getText())) {
            return tipCustomRepository.updateText(tip.getTipId(), text);
        }
        return false;
    }

    private boolean updateGame(Tip tip, Game game) {
        if (tip.getGame().getGameId() != game.getGameId()) {
            return tipCustomRepository.updateGame(tip.getTipId(), game.getGameId());
        }
        return false;
    }

    private boolean updateTipman(Tip tip, Tipman tipman) {

        if (tip.getTipman().getTipmanId() != tipman.getTipmanId()) {
            return tipCustomRepository.updateTipman(tip.getTipId(), tipman.getTipmanId());
        }
        return false;
    }

    private boolean updateType(Tip tip, ETipType type) {
        if (tip.getType() != type) {
            return tipCustomRepository.updateType(tip.getTipId(), type);
        }
        return false;
    }

    private boolean makeAndUpdateHotMatch(Tip tip, Document tipDoc) {
        if (tip.getHotMatch() == null) {
            Boolean newHotMatchFlag = makeHotMatchFlag(tipDoc);
            if (newHotMatchFlag != null) {
                return tipCustomRepository.updateHotMatch(tip.getTipId(), newHotMatchFlag);
            }
        }
        return false;
    }

    private boolean makeAndUpdateOdds(Tip tip, Document tipDoc) {
        if (tip.getOdds() == null || tip.getOdds() == ODDS_UNKNOWN || tip.getOdds().equals(0D)) {
            Double newOdds = makeAndUpdateOdds(tip.getPick(), tipDoc);
            if (newOdds != null) {
                return tipCustomRepository.updateOdds(tip.getTipId(), newOdds);
            }
        }
        return false;
    }

    private boolean makeAndUpdatePick(Tip tip, Document tipDoc) {
        Element spotDiv = tipDoc.selectFirst("div[data-tabconnect=spot]");
        if (tip.getPick() == null || tip.getPick().equals(EPick.UNKNOWN)) {
            EPick newPick = makePickForMatchMonye(spotDiv, ETipType.SPOT, tip.getGame(), tipDoc);
            if (newPick != null && !EPick.UNKNOWN.equals(newPick)) {
                log.info("Updating pick for tip {}! New pick {}", tip.getTipId(), newPick);
                return tipCustomRepository.updatePick(tip.getTipId(), newPick);
            }
        }
        return false;
    }

    private boolean makeGameScore(Document tipDoc, Game game) {
        if (game.getScore() == null) {
            game.setScore(scoreService.makeScore(tipDoc.selectFirst("div.event")));
            if (Objects.nonNull(game.getScore())) {
               return true;
            }
        }
        return false;
    }

    private Boolean makeHotMatchFlag(Document tipDoc) {
        Element hotMatchEl = tipDoc.selectFirst("div.hot-ribbon");
        return hotMatchEl != null && hotMatchEl.hasText();
    }

    /**
     * Creates spot tip
     *
     * @param tipDoc страничка матча
     * @return Тип
     */
    private Tip createTip(Document tipDoc, Tipman tipman, Game game) {

        Objects.requireNonNull(tipman, "Tipman must be not empty!");
        Objects.requireNonNull(tipDoc, "TipDoc must be not empty!");
        Objects.requireNonNull(game, "Game must be not empty!");

        /////////// making tips ////////////////////
//        Element analisisDiv = tipDoc.selectFirst(
//                "div.tab-analysis, " +
//                        "div.tab-container, " +
//                        "div.tertiary-reverse, " +
//                        "div.general-tab");

        ///////////////////////////////////////////////////////////////////////////////////////////////
        Element spotDiv = tipDoc.selectFirst("div[data-tabconnect=spot]");
        // MAYBE Element goalDiv = analisisDiv.selectFirst("div[data-tabconnect=goal]");
        // MAYBE Element specialDiv = analisisDiv.selectFirst("div[data-tabconnect=special]");

        ///////////////// score ////////////////////////
        Score score = null;
        try {
            score = scoreService.makeScore(tipDoc.selectFirst("div.event"));
            game.setScore(score);
            game = gameService.update(game);
        } catch (Exception e) {
            log.error("Error making score for game {}", game.getLink(), e);
        }

        ///////////////// pick ////////////////////////
        EPick spotPick = null;
        try {
            spotPick = makePickForMatchMonye(spotDiv, ETipType.SPOT, game, tipDoc);
        } catch (Exception e) {
            log.error("Error making MM spot pick for game {}", game.getLink(), e);
        }

        ///////////////// odds ////////////////////////
        Double spotOdds = null;
        try {
            spotOdds = makeAndUpdateOdds(spotPick, tipDoc);
        } catch (Exception e) {
            log.error("Error making spot odds for game {}", game.getLink(), e);
        }

        ///////////////// hot match ///////////////////
        Boolean hotMatch = null;
        try {
            hotMatch = makeHotMatchFlag(tipDoc);
        } catch (Exception e) {
            log.error("Error making hot match flag for game {}", game.getLink(), e);
        }

        Tip spotTip = Tip.builder()
                .text(Utils.limitTextTo253(spotDiv.text()))
                .strong(getStrongText(spotDiv, true))
                .type(ETipType.SPOT)
                .pick(spotPick)
                .tipman(tipman)
                .game(game)
                .odds(spotOdds)
                .hotMatch(hotMatch)
                .build();

        if (Objects.nonNull(game.getScore()) && spotPick != EPick.UNKNOWN && spotPick != EPick.NOBET) {
             makeAndUpdateStatus(spotTip, game.getScore(), false);
        }

        // check that status is saved
        updateFetchStatus(spotTip, false);
        tipRepository.save(spotTip);

        log.info("Tip spot: {}", spotTip);
        return spotTip;
    }

    private Double makeAndUpdateOdds(EPick spotPick, Document tipDoc) {
        Objects.requireNonNull(tipDoc);

        Element opapOddsDiv = tipDoc.selectFirst("div.match-odds");
        if (opapOddsDiv != null) {
            Elements oddsElements = opapOddsDiv.select("div.row-value");
            if (Objects.nonNull(oddsElements)) {
                switch (spotPick) {
                    case SPOT_1:
                        return Double.valueOf(oddsElements.get(1).text());
                    case SPOT_2:
                        return Double.valueOf(oddsElements.get(3).text());
                    case SPOT_X:
                        return Double.valueOf(oddsElements.get(2).text());
                    case NOBET:
                        return ODDS_NO_BET;
                    case TODO:
                        return ODDS_TODO;
                    case UNKNOWN:
                    case SPOT_DNB_1:
                    case SPOT_DNB_2:
                    default:
                        return makeOddsFromSpotDivStrongText(tipDoc);
                }
            }
        }
        return makeOddsFromSpotDivStrongText(tipDoc);
    }

    private Double makeOddsFromSpotDivStrongText(Element tipDoc) {
        Objects.requireNonNull(tipDoc, "Tip Doc!");
        Element spotDiv = tipDoc.selectFirst("div[data-tabconnect=spot]");
        String strongText = getStrongText(spotDiv, false);
        return parseOddFromText(strongText);
    }


    private boolean makeAndUpdateStatus(Tip tip, Score score, boolean shouldUpdateTip) {
        ETipStatus newStatus;
// check the status for OPEN tips after they are played
        if (score == null) {
            newStatus = ETipStatus.OPEN;
        } else if (score.getScoreType() == EScoreType.GAME_CANCELLED) {
            newStatus = ETipStatus.GAME_CANCELLED;
        } else if (score.getScoreType() == EScoreType.GAME_POSTPONED) {
            newStatus = ETipStatus.GAME_POSTPONED;
        } else {
            switch (tip.getPick()) {
                case SPOT_1:
                        if (score.getHomeGoals() > score.getAwayGoals()) {
                            newStatus = ETipStatus.WON;
                        } else {
                            newStatus = ETipStatus.LOST;
                        }
                    break;
                case SPOT_X:

                        if (score.getHomeGoals() == score.getAwayGoals()) {
                            newStatus = ETipStatus.WON;
                        } else {
                            newStatus = ETipStatus.LOST;
                        }
                    break;
                case SPOT_2:
                        if (score.getHomeGoals() < score.getAwayGoals()) {
                            newStatus = ETipStatus.WON;
                        } else {
                            newStatus = ETipStatus.LOST;
                        }
                    break;
                case SPOT_DNB_1:
                        if (score.getHomeGoals() > score.getAwayGoals()) {
                            newStatus = ETipStatus.WON;
                        } else if (score.getHomeGoals() < score.getAwayGoals()) {
                            newStatus = ETipStatus.LOST;
                        } else {
                            newStatus = ETipStatus.DNB;
                        }
                    break;
                case SPOT_DNB_2:
                        if (score.getHomeGoals() > score.getAwayGoals()) {
                            newStatus = ETipStatus.LOST;
                        } else if (score.getHomeGoals() < score.getAwayGoals()) {
                            newStatus = ETipStatus.WON;
                        } else {
                            newStatus = ETipStatus.DNB;
                        }
                    break;
                case SPOT_2X:
                    if (score.getHomeGoals() > score.getAwayGoals()) {
                        newStatus = ETipStatus.LOST;
                    } else {
                        newStatus = ETipStatus.WON;
                    }
                    break;
                case SPOT_1X:
                    if (score.getHomeGoals() < score.getAwayGoals()) {
                        newStatus = ETipStatus.LOST;
                    } else {
                        newStatus = ETipStatus.WON;
                    }
                    break;
                case NOBET:
                    newStatus = ETipStatus.NOBET;
                    break;
                case UNKNOWN:
                case TODO:
                default:
                    newStatus = ETipStatus.UNKNOWN;
            }
        }
        log.info("-- TIP STATUS -- OLD: {}; NEW: {}", tip.getStatus(), newStatus);
        if (newStatus != tip.getStatus()) {
            if (shouldUpdateTip) {
                return tipCustomRepository.updateStatus(tip.getTipId(), newStatus);
            } else {
                tip.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    private String getStrongText(Element el, boolean shouldTrimTextTo253) {
        Elements strongTextSpotDivEls = el.select("strong, b");
        if (strongTextSpotDivEls.size() > 0) {
            String strong = StringUtils.collectionToDelimitedString(strongTextSpotDivEls.eachText(), " ");
            if (shouldTrimTextTo253) {
                return Utils.limitTextTo253(strong);
            } else {
                return strong;
            }
        }
        return null;
    }

    // TODO
    //  Использовать рекомендованные котировки для определение PICK для тех у которых четко не понятно что за пик

    private EPick makePickForMatchMonye(Element tipEl, ETipType type, Game game, Document tipDoc) {
        String strongText = getStrongText(tipEl, false);
        if (StringUtils.isEmpty(strongText)) {
            return EPick.UNKNOWN;
        }
        if (textHasAnyOfWords(strongText, M_M_XORIS_NOIMA, M_M_NO_BET, M_M_NO_BET_2, M_M_NO_BET_3, M_M_NO_BET_4, M_M_PASO, M_M_MHN_APODEXTO, M_M_MH_APODEXTO, M_M_DEN_APODEXTO, M_M_APOFIGI, M_M_XAMILO, M_M_XAMILA, M_M_ZORIKO, M_M_ASMFIROPO, M_M_DEN_THA_ASXOLITHO, M_M_XORIS_AKSIA,
                M_M_ASXOLITHOYME, M_M_DEN_THA_PARO, M_M_DEN_EXEI_AKSIA, M_M_DEN_YPARXEI_AKSIA, M_M_DEN_PEZETE, M_M_MIKROS_ASOS, M_M_DISKOLO,
                M_M_KAMIA_AKSIA, M_M_KAMIA_EMPLOKH, M_M_KANENAS_LOGOS_EMPLOKHS, M_M_TO_AFINO, M_M_APEZOUME, M_M_APOFIGOUME, M_M_APOXH, M_M_AMETOXOUS,
                M_M_MHN_PONTAROUME, M_M_KANENA_ENDIAFERON, M_M_MHN_ANAMIXTOUME, M_M_GRIFOS, M_M_MAKRIA, M_M_DEN_VRISKETE, M_M_NA_MPLEKSO,
                M_M_TO_AFINOUME, M_M_DE_THA_TO_EPEZA, M_M_TRIPLI, M_M_PROSPERASI, M_M_ALLA, M_M_N_O_BET, M_M_DEN_AKSIZI,
                M_M_DEN_MAS_LENE, M_M_DEN_VLEPO, M_M_DEN_INE, M_M_STIN_AKRH, M_M_DEN_KRATAME, M_M_KAKO, M_M_DEN_BORUME, M_M_DEN_VGENEI, M_M_DEN_BORO, M_M_MENO_EKTOS,
                M_M_ADIAFORO,M_M_MPERDEMA,M_M_MPERDEMENO,M_M_PROSPERNAME,M_M_DEN_EXEI_NOIMA,M_M_DEN_THA_MPLEKSO, M_M_EMPLEKOYME, M_M_DEN_THA_MPLEKSOYME,M_M_DEN_PROKITE_ASXOLITHO,
                M_M_DEN_GIA_PARATIRISI, M_M_DEN_PROS_PARATIRISI

        )) {
            return EPick.NOBET;
        }
        Element bookieLink = tipEl.select("a").first();
        boolean strongTextHasBookieName = textHasAnyOfBookieName(strongText);
        if (strongTextHasBookieName || bookieLink != null && bookieLink.hasText()) {
            switch (type) {
                case SPOT:
                    if (textHasAnyOfWords(strongText, M_M_ASOS, M_M_ASO, M_M_ASOU, M_M_NIKI_TIS_EDRAS, M_M_ARISTERA)) {
                        return EPick.SPOT_1;
                    } else if (textHasAnyOfWords(strongText, M_M_DIPLO, M_M_TOU_DIPLOU, M_M_DEKSIA)) {
                        return EPick.SPOT_2;
                    } else if (textHasAnyOfWords(strongText, M_M_TO_X, M_M_ISOPALIA)) {
                        return EPick.SPOT_X;
                    } else if (textHasAnyOfWords(strongText, M_M_1__DNB, M_M_ASIATIKO_1, M_M_1_DNB, M_M_ASOS_DNB, M_M_ASO_DNB)) {
                        return EPick.SPOT_DNB_1;
                    } else if (textHasAnyOfWords(strongText, M_M_1X, M_M_X1)) {
                        return EPick.SPOT_1X;
                    } else if (textHasAnyOfWords(strongText, M_M_2X, M_M_X2)) {
                        return EPick.SPOT_2X;
                    } else if (textHasAnyOfWords(strongText, M_M_2__DNB, M_M_2_DNB, M_M_ASIATIKO_2, M_M_DIPLO_DNB)) {
                        return EPick.SPOT_DNB_2;
                    } else {
                        Double odds = parseOddFromText(strongText);
                        if (odds != null) {
                            if (textHasAnyOfWords(strongText, M_M_THA_STHRIKSOYME,M_M_STOIXIMAN,M_M_INTERWETTEN,M_M_BET_365,M_M_BET365,M_M_NOVIBET,M_M_PAMESTOIXMA,M_M_PAMESTOIXIMA)) {
                                EPick pick = determinePickByOdds(tipDoc, odds, 0.2);
                                if (!pick.equals(EPick.UNKNOWN)) {
                                    return pick;
                                }
                            }
                            if (textHasAnyOfWords(strongText, M_M_TO_DE, M_M_TO_D_E_, M_M_PLUS_0)) {
                                EPick pick = determinePickByOdds(tipDoc, odds, 1.3);
                                if (pick.equals(EPick.SPOT_1)) {
                                    return EPick.SPOT_DNB_1;
                                }
                                if (pick.equals(EPick.SPOT_2)) {
                                    return EPick.SPOT_DNB_2;
                                }
                            }
                        }
                        if (textHasWord(strongText, M_M_FAVORI)) {
                            if (textHasAnyOfWords(strongText, M_M_GIPEDOUXOS, M_M_GIPEDOUXI)) {
                                return EPick.SPOT_1;
                            }
                            if (textHasAnyOfWords(strongText, M_M_FILOXENOUMENH, M_M_FILOXENOUMENOS)) {
                                return EPick.SPOT_2;
                            }

                            if (odds != null) {
                                String homeTeamNaemGr = game.getHomeTeam().getNameGr();
                                String awayTeamNameGr = game.getAwayTeam().getNameGr();
                                for (String part : strongText.split(" ")) {
                                    String greekToUpperPart = greekToUpper(part);
                                    if (wordsAreSimillar(homeTeamNaemGr, greekToUpperPart)) {
                                        return EPick.SPOT_1;
                                    } else if (wordsAreSimillar(awayTeamNameGr, greekToUpperPart)) {
                                        return EPick.SPOT_2;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case GOAL:
                    // TODO determine goal_3+, goal_0-1...
                    return EPick.TODO;
                case SPECIAL:
                    // TODO determine ???
                    return EPick.TODO;
            }
        }
        // if we cant determine
        return EPick.UNKNOWN;
    }

    private EPick determinePickByOdds(Document tipDoc, Double odds, double e) {
        Objects.requireNonNull(tipDoc);
        Element opapOddsDiv = tipDoc.selectFirst("div.match-odds");
        if (Objects.isNull(opapOddsDiv)) {
            return EPick.UNKNOWN;
        }
        Elements oddsElements = opapOddsDiv.select("div.row-value");
        if (Objects.isNull(oddsElements)) {
            return EPick.UNKNOWN;
        }
        Double odd_spot_1_distance = Math.abs(odds - parseDouble(oddsElements.get(1).text()));
        Double odd_spot_2_distance = Math.abs(odds - parseDouble(oddsElements.get(3).text()));
        Double odd_spot_X_distance = Math.abs(odds - parseDouble(oddsElements.get(2).text()));
        Objects.requireNonNull(odd_spot_1_distance);
        Objects.requireNonNull(odd_spot_2_distance);
        Objects.requireNonNull(odd_spot_X_distance);

        return Stream
                .of(odd_spot_1_distance, odd_spot_2_distance, odd_spot_X_distance)
                .min(Double::compareTo)
                .map(o -> {
                    if (o > e) {
                        // погрешность
                        return EPick.UNKNOWN;
                    }
                    if (o.equals(odd_spot_1_distance)) {
                        return EPick.SPOT_1;
                    } else if (o.equals(odd_spot_2_distance)) {
                        return EPick.SPOT_2;
                    } else if (o.equals(odd_spot_X_distance)) {
                        return EPick.SPOT_X;
                    } else {
                        return EPick.UNKNOWN;
                    }})
                .get();
    }

    private boolean textHasAnyOfBookieName(String strongText) {
        Iterable<Bookie> bookies = bookieRepository.findAll();
        for (Bookie bookie : bookies) {
            if (textHasWord(strongText, bookie.getCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Critiria for Fully_Fetched - not empty: hotMatch, pick, odds, status, text, strong, type, tipman, game
     *
     * @param tip
     * @return
     */
    public boolean updateFetchStatus(Tip tip, boolean shouldUpdateTip) {
        Objects.requireNonNull(tip, "Tip must not be empty");
        EFetchStatus status = makeFetchStatus(tip.getPick(),
                tip.getOdds(),
                tip.getStatus(),
                tip.getText(),
                tip.getType(),
                tip.getTipman(),
                tip.getGame(),
                tip.getHotMatch()
        );
        if (status != tip.getFetchStatus()) {
            if (shouldUpdateTip) {
                return tipCustomRepository.updateFetchStatus(tip.getTipId(), status);
            } else {
                tip.setFetchStatus(status);
                return true;
            }
        }
        return false;
    }


}
