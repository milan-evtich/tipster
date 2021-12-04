package com.milan.tipster.controller;

import com.milan.tipster.dao.FaultRepository;
import com.milan.tipster.model.Game;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.enums.ETipStatus;
import com.milan.tipster.service.FetchingService;
import com.milan.tipster.service.GameService;
import com.milan.tipster.service.TipService;
import com.milan.tipster.util.Constants;
import com.milan.tipster.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.milan.tipster.util.Constants.*;

@Slf4j
@RestController
public class TipController {

    @Autowired
    private GameService gameService;

    @Autowired
    private TipService tipService;

    @Autowired
    private FaultRepository faultRepository;

    @Autowired
    private FetchingService fetchingService;

    @GetMapping(value = "/tips/fetch-from-files")
    public String fetchTipsFromFiles() throws IOException {
        String resultFormat = "%s number of tips has been updated!";
        int count = 0;
        Tip tip = null;
        File folder = new File(DEFAULT_FILE_STORAGE_DIR + GAMES_DIR_NAME);
        Objects.requireNonNull(folder, "Folder!");
        for (final File fileEntry : folder.listFiles()) {
            String fileName = fileEntry.getName();
            String absoluteFilePath = fileEntry.getAbsolutePath();
            log.info("Fetching tip from file: {}", fileEntry.getAbsolutePath());
            Document tipDoc = fetchingService.fetchDocByUrlOrPath(absoluteFilePath, true);
            // TODO UNCOMENT AND TEST
            Boolean tipUpdated = tipService.updateTipFromFile(tipDoc, fileName, absoluteFilePath);
            if (tipUpdated) {
                log.info("Successfully updated tip {} from file {}", tip, fileName);
                count++;
            } else {
                log.info("No tip been updated from file {}", fileName);
            }
        }
        return String.format(resultFormat, count);
    }

    /**
     * Fetching tips for all games without tips
     * @return
     */
    @GetMapping(value = "/tips/fetch-all-tips-for-games-without-tips")
    public int fetchTipsForGamesWithoutTips() {
        return tipService.fetchTipsWithGameFetchStatusPartlyFetched(parseFetchFileOrUrl("URL"));
    }

    /**
     * Fetching tips for games with status open and datetime game played already
     * @return
     */
    @GetMapping(value = "/tips/fetch/{fileOrUrl}/status/{status}")
    public int fetchOpenTipsThatPlayedAlready(@PathVariable String fileOrUrl, @PathVariable ETipStatus status) {
        return tipService.fetchTipsWithStatusAndPlayedAlready(parseFetchFileOrUrl(fileOrUrl), status);
    }

    /**
     * Fetching tips for games with status open and game not played
     * @return
     */
    @GetMapping(value = "/tips/fetch-open-not-played-yet/{fileOrUrl}/status/{status}")
    public int fetchTipsNotPlayedYet(@PathVariable String fileOrUrl, @PathVariable ETipStatus status) {
        return tipService.fetchTipsWithStatusAndNotPlayedYet(parseFetchFileOrUrl(fileOrUrl), status);
    }
    /**
     * Fetching tips for games with bad link syntaxis without playedOn date
     * @return
     */
    @GetMapping(value = "/tips/fetch/{fileOrUrl}/playedOnNull")
    public int fetchTipsPlayedOnNull(@PathVariable String fileOrUrl) {
        return tipService.fetchTipsPlayedOnNull(parseFetchFileOrUrl(fileOrUrl));
    }

    /**
     * Fetching tips for games with UNKNOWN pick
     * @return
     */
    @GetMapping(value = "/tips/fetch/{fileOrUrl}/pickUnknown")
    public int fetchTipsPickUnknown(@PathVariable String fileOrUrl) {
        return tipService.fetchTipsPickUnknown(parseFetchFileOrUrl(fileOrUrl));
    }

    /**
     * Fetching tips for games with fetched_status = PARTLY_FETCHED
     * @return
     */
    @GetMapping(value = "/fetch_tips/game_status_partly_fetched/{fileOrUrl}")
    public int fetchTipsWithGameFetchStatusPartlyFetched(@PathVariable String fileOrUrl) {
        return tipService.fetchTipsWithGameFetchStatusPartlyFetched(parseFetchFileOrUrl(fileOrUrl));
    }

    /**
     * Fetching tips with fetched_status = PARTLY_FETCHED
     * @return
     */
    @GetMapping(value = "/fetch_tips/tip_status_partly_fetched/{fileOrUrl}")
    public int fetchTipsWithStatusPartlyFetched(@PathVariable String fileOrUrl) {
        return tipService.fetchTipsWithFetchStatusPartlyFetched(parseFetchFileOrUrl(fileOrUrl));
    }

     /**
     * Fetching tips with odds = null
     * @return
     */
    @GetMapping(value = "/tips/fetch/{fileOrUrl}/odds")
    public int fetchTipsWithOddsNull(@PathVariable String fileOrUrl) {
        return tipService.fetchTipsWithOddsNull(parseFetchFileOrUrl(fileOrUrl));
    }

    private boolean parseFetchFileOrUrl(String fileOrUrl) {
        boolean isFile = false;
        if (StringUtils.isNoneBlank(fileOrUrl) && fileOrUrl.equals(Constants.PARAM_FETCH_FROM_FILE)) {
            isFile = true;
        }
        return isFile;
    }

    @GetMapping(value = "/tips/fetch/{fileOrUrl}/{playedOnStart}/{playedOnEnd}")
    public String fetchTipsOnDate(@PathVariable String playedOnStart,
                                  @PathVariable String playedOnEnd,
                                  @PathVariable String fileOrUrl) {

        LocalDate startDatePlayedOn = Utils.convertStringToLocalDate(playedOnStart);
        LocalDate endDatePlayedOn = Utils.convertStringToLocalDate(playedOnEnd);

        if (endDatePlayedOn.isBefore(startDatePlayedOn)) {
            throw new InvalidParameterException();
        }
        LocalDate localDate = startDatePlayedOn;
        int tipsFetchedCount = 0;
        while (localDate.isBefore(endDatePlayedOn)) {
            tipsFetchedCount = tipsFetchedCount + tipService.fetchTipsOnDate(localDate, parseFetchFileOrUrl(fileOrUrl));
            localDate = localDate.plusDays(1);
        }
        String message = "Fetch tips on date - " + tipsFetchedCount + " tips fetched.";
        log.info(message);
        return message;
    }

    /**
     * Fetching tips played on specific date
     * If tip is found and not been saved before, save it to DB and store the html file
     *
     * @param played - date for searching the tips
     * @return - a list of newly created tips
     */
    @GetMapping(value = "/tips/fetch/{fileOrUrl}/{played}")
    public String fetchTipsOnDate(@PathVariable String played, @PathVariable String fileOrUrl) {
        LocalDate datePlayedOn = Utils.convertStringToLocalDate(played);
        List<Game> games = gameService.findGamesOnDate(datePlayedOn);
        int tipsFetchedCount = tipService.fetchTipsForGames(games, parseFetchFileOrUrl(fileOrUrl));
        return "Fetch tips on date - " + tipsFetchedCount + " tips fetched.";
    }

}
