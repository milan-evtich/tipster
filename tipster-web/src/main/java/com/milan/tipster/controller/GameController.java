package com.milan.tipster.controller;

import com.milan.tipster.dto.FetchGamesResponse;
import com.milan.tipster.model.Game;
import com.milan.tipster.service.FetchingService;
import com.milan.tipster.service.FileStorageService;
import com.milan.tipster.service.GameService;
import com.milan.tipster.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.milan.tipster.util.Constants.*;

@Slf4j
@RestController
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private FetchingService fetchingService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Находит матчей на страничке последних анализов начиная с текущей датой и заканчивая с {endDate}
     * Создает матчей если не существуют в БД
     * Если на страничке был создан хоть один новый матч то сохраняеть файл html с страничкой
     */
 /*   @GetMapping(value = "/games/fetch/{endDateStr}")
    public ResponseEntity<String> fetchNewGamesTillEndDate(@PathVariable String endDateStr) throws IOException {
        LocalDate endDate = Utils.convertStringToLocalDate(endDateStr);
        LocalDate endDateLimit = LocalDate.now().minusDays(30);
        if (endDate == null) {
            endDate = LocalDate.now().minusDays(1);
        } else {
            if (!endDate.isBefore(LocalDate.now())) {
                endDate = LocalDate.now().minusDays(1);
            } else if (endDate.isBefore(endDateLimit)) {
                endDate = endDateLimit;
            }
        }
        LocalDate fetchedDate = LocalDate.now();
        int pageNum = 1;
        int gamesFetchedCount = 0;
        while (!fetchedDate.isBefore(endDate)) {
            List<Game> fetchedGames = fetchLatestGamesPerPage(pageNum++);
            if (!fetchedGames.isEmpty())
            gamesFetchedCount += fetchedGames.size();
            LocalDateTime currentOldestDate = fetchedGames.stream().map(Game::getPlayedOn).min(LocalDateTime::compareTo)
                    .orElse(endDate.atStartOfDay());
            fetchedDate = currentOldestDate.toLocalDate();
        }
        return new ResponseEntity<String>("Fetched " + gamesFetchedCount + " new games.", HttpStatus.OK);

    }*/

    /**
     * Находит матчей на страничке последних анализов и создает их если раньше не были созданы
     * Если хотя бы один новый матч был создан тогда сохраняет и страничку в виде html документа
     *
     * @param startPage - начальная страничка анализов
     * @param endPage   - окончательная страничка анализов
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/games/fetch/{startPage}/{endPage}")
    public ResponseEntity<FetchGamesResponse> fetchGames(@PathVariable Integer startPage, @PathVariable Integer endPage) throws IOException {
        FetchGamesResponse fetchGamesResponse = FetchGamesResponse.builder()
                .build();
        for (int page = startPage; page <= endPage; page++) {
            String url = URL_MATCH_MONEY_MOBILE_VERSION + PATH_LATEST_GAMES + page;
            log.info("Fetching games with url: {}", url);
            Document latestAnalysisDoc = fetchingService.fetchDocByUrlOrPath(url, false);
            gameService.fetchGames(latestAnalysisDoc, fetchGamesResponse);
        }
        return ResponseEntity.ok(fetchGamesResponse);
    }

    private List<Game> fetchLatestGamesPerPage(int page) throws IOException {
        List<Game> games = new ArrayList<>();
        String url = URL_MATCH_MONEY_MOBILE_VERSION + PATH_LATEST_GAMES + page;
        log.info("Fetching games with url: {}", url);
        Document latestAnalysisDoc = fetchingService.fetchDocByUrlOrPath(url, false);
        games.addAll(gameService.fetchGames(latestAnalysisDoc));
        if (games.size() > 0) {
            log.info("Successfully fetched {} games on url {}", games.size(), url);
//  SAVING FILE ON DISK
//            fileStorageService.saveToFileIfNotExists(latestAnalysisDoc,
//                    DEFAULT_FILE_STORAGE_DIR + LATEST_ANALYSIS_DIR_NAME + "/" + FILE_NAME_PREFIX_LATEST_GAMES,
//                    true);

        } else {
            log.warn("No games been fetched from url {}", url);
        }
        return games;
    }

    @GetMapping(value = "/games/fetch-from-files")
    public List<Game> fetchGamesFromFiles() throws IOException {
        List<Game> games = new ArrayList<>();
        File folder = new File(DEFAULT_FILE_STORAGE_DIR + LATEST_ANALYSIS_DIR_NAME);
        Objects.requireNonNull(folder);
        for (final File fileEntry : folder.listFiles()) {
            String fileName = fileEntry.getName();
            if (fileName.startsWith(FILE_NAME_PREFIX_LATEST_GAMES)) {
                log.info("Fetching games from file: {}", fileEntry.getAbsolutePath());
                Document latestAnalysisDoc = fetchingService.fetchDocByUrlOrPath(fileEntry.getAbsolutePath(), true);
                games.addAll(gameService.fetchGames(latestAnalysisDoc));
                if (games.size() > 0) {
                    log.info("Successfully fetched {} games from file {}", games.size(), fileEntry.getName());

                } else {
                    log.error("No games been fetched from file {}", fileEntry.getName());
                }
//                fileStorageService.moveFileToDir(fileName, DEFAULT_FILE_STORAGE_DIR + LATEST_ANALYSIS_DIR_NAME);
            }
        }
        return games;
    }


}
