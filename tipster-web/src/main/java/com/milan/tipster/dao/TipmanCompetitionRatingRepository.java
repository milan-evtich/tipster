package com.milan.tipster.dao;

import com.milan.tipster.model.TipmanCompetitionRating;

import java.util.List;

public interface TipmanCompetitionRatingRepository {

    void createNewTipmanCompetitionRating(Long tipmanId, Long competitionId);

    TipmanCompetitionRating findTipmanCompetitionRating(Long tipmanId, Long competitionId);

    List<TipmanCompetitionRating> findAll();
}
