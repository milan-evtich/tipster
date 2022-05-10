package com.milan.tipster.service;

import com.milan.tipster.dto.FetchGamesResponse;
import com.milan.tipster.model.Game;
import com.milan.tipster.model.Score;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.EPick;
import org.jsoup.nodes.Document;

import java.time.LocalDate;
import java.util.List;

public interface GameService {

    List<Game> findGamesWithoutDateInLink();

    List<Game> fetchGames(Document analysisDoc);

    void fetchGames(Document analysisDoc, FetchGamesResponse fetchGamesResponse);

    List<Game> findGamesOnDate(LocalDate localDate);

    List<Game> findGamesByFetchStatus(EFetchStatus status);

    Game saveIfNotExist(Game game);

    boolean updatePlayedOnLocalDateTime(Game game, Document gameDoc);

    Game update(Game game);

    Game findGameByCode(String gameCode);

    Game fetchGameFromTipDoc(Document tipDoc, String gameCode);

    boolean updateNameGrFromTeams(Game game);

    Game updateFetchStatus(Game game, boolean needBDUpdate);

    boolean updateScore(Game game);

    List<Game> findGamesByPick(EPick pick);

}
