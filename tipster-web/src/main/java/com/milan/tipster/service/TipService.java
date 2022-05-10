package com.milan.tipster.service;

import com.milan.tipster.dto.FetchTipsForGamesWithoutTipsResponse;
import com.milan.tipster.dto.PredictionTipDto;
import com.milan.tipster.model.Game;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.enums.ETipFilter;
import com.milan.tipster.model.enums.ETipStatus;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TipService {

    int fetchTipsOnDate(LocalDate datePlayedOn, boolean fetchFromFile);

    int fetchTipsPlayedOnNull(boolean fetchFromFile);

    int fetchTipsWithGameFetchStatusPartlyFetched(boolean fetchFromFile);

    FetchTipsForGamesWithoutTipsResponse fetchAllTipsWithGameFetchStatusPartlyFetched(boolean fetchFromFile);

    int fetchTipsForGames(List<Game> games, boolean fetchFromFile);

    FetchTipsForGamesWithoutTipsResponse fetchTipsWithErrorsForGames(List<Game> games, boolean fetchFromFile);

    boolean fetchAndSaveOrUpdateTip(Game game, boolean fetchFromFile) throws IOException;

    Boolean updateTipFromFile(Document tipDoc, String fileName, String absoluteFilePath);

    List<Tip> findAllUnratedTips();

    int fetchTipsPickUnknown(boolean fileOrUrl);

    int fetchTipsWithFetchStatusPartlyFetched(boolean fetchFromFile);

    int fetchTipsWithOddsNull(boolean parseFetchFileOrUrl);

    List<PredictionTipDto> getTipsPredictionForToday(int top, Integer hours, Integer minutes, ETipFilter filter);

    List<PredictionTipDto> getTipsPredictionForPeriod(int top, LocalDateTime start, LocalDateTime end);

    int fetchTipsWithStatusAndPlayedAlready(boolean parseFetchFileOrUrl, ETipStatus status);

    int fetchTipsWithStatusAndNotPlayedYet(boolean parseFetchFileOrUrl, ETipStatus status);

}
