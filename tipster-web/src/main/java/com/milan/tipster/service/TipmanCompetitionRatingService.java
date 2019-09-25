package com.milan.tipster.service;

import com.milan.tipster.model.TipmanCompetitionRating;

public interface TipmanCompetitionRatingService {

    TipmanCompetitionRating findTipmanCompetitionRating(Long tipmanId, Long competitionId);

    void createNewTipmanCompetitionRating(Long tipmanId, Long competitionId);
}
