package com.milan.tipster.service;

import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.Tipman;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface RatingService {

    /** Update rating based on tips for some period of time
     *
     * @param startDateTime beginning date with time of search period - till now
     * @return count of rated tips
     */
    int rateBaseOnTipsSinceDate(LocalDateTime startDateTime);

    int rateTop100UnratedTips();

    /**
     * Creates complex (TipmanCompetitionRating) rating for tipman and competition
     *
     * @param tipman
     * @param competition
     */
    void createTipmanCompetitionRatingIfNotExist(Tipman tipman, Competition competition);

}
