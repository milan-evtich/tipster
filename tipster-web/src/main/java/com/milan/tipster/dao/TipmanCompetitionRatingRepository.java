package com.milan.tipster.dao;

import com.milan.tipster.model.TipmanCompetitionRating;

public interface TipmanCompetitionRatingRepository {

    void createNewTipmanCompetitionRating(Long tipmanId, Long competitionId);

    TipmanCompetitionRating findTipmanCompetitionRating(Long tipmanId, Long competitionId);
}
