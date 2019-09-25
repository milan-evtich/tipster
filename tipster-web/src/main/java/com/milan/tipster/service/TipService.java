package com.milan.tipster.service;

import com.milan.tipster.model.Game;
import com.milan.tipster.model.Tip;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface TipService {

    int fetchTipsOnDate(LocalDate datePlayedOn, boolean fetchFromFile);

    int fetchTipsPlayedOnNull(boolean fetchFromFile);

    int fetchTipsWithGameFetchStatusPartlyFetched(boolean fetchFromFile);

    int fetchTipsForGames(List<Game> games, boolean fetchFromFile);

    boolean fetchAndSaveOrUpdateTip(Game game, boolean fetchFromFile) throws IOException;

    Boolean updateTipFromFile(Document tipDoc, String fileName, String absoluteFilePath);

    List<Tip> findAllUnratedTips();

    int fetchTipsPickUnknown(boolean fileOrUrl);

    int fetchTipsWithFetchStatusPartlyFetched(boolean fetchFromFile);

    int fetchTipsWithOddsNull(boolean parseFetchFileOrUrl);
}
