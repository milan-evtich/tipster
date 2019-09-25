package com.milan.tipster.dao;

import com.milan.tipster.model.Game;
import com.milan.tipster.model.Score;
import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.EPick;

import java.time.LocalDateTime;
import java.util.List;

public interface GameCustomeRepository {

    boolean updatePlayedOn(Long gameId, LocalDateTime playedOn);

    boolean updateNameGr(Long gameId, String gameNameGr);

    boolean updateFetchStatus(Long gameId, EFetchStatus fetchStatus);

    boolean updateScore(Long gameId, Score score);

    List<Game> findGamesByPick(EPick unknown);
}
