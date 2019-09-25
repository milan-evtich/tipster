package com.milan.tipster.dao;

import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.ETipStatus;
import com.milan.tipster.model.enums.ETipType;

public interface TipCustomRepository {

    boolean updatePick(Long tipId, EPick pick);

    boolean updateOdds(Long tipId, Double odds);

    boolean updateHotMatch(Long tipId, Boolean hotMatchFlag);

    boolean updateStatus(Long tipId, ETipStatus status);

    boolean updateType(Long tipId, ETipType type);

    boolean updateTipman(Long tipId, Long tipmanId);

    boolean updateGame(Long tipId, Long gameId);

    boolean updateText(Long tipId, String text);

    boolean updateStrong(Long tipId, String strongText);

    boolean updateFetchStatus(Long tipId, EFetchStatus status);
}
