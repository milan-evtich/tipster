package com.milan.tipster.service;

import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.Tipman;

public interface RatingService {

    /**
     * Creates complex (TipmanCompetitionRating) rating for tipman and competition
     *
     * @param tipman
     * @param competition
     */
    void createTipmanCompetitionRatingIfNotExist(Tipman tipman, Competition competition);

    /**
     * Updates complex (TipmanCompetitionRating) rating for tipman and competition
     * @param tipId
     * @return
     */
    boolean updateTCRating(Long tipId);

    /**
     * Updates tipman rating
     * @param tipId
     * @return
     */
    boolean updateTipmanRating(Long tipId);

    /**
     * Updates competition rating
     * @param tipId
     * @return
     */
    boolean updateCompetitionRating(Long tipId);
}
