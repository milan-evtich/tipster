package com.milan.tipster.model;

import java.util.function.Predicate;

public interface Rateable {

    static Predicate<Rateable> rateableActive() {
        return rateable -> !rateable.getRating().getOverallCoefficient().equals(0D);
    }

    Rating getRating();

    default double rateRateable() {
        if (getRating().getOverallTipCount() > 0L) {
            Long tipsLost = getRating().getTipsLost();
            Long tipsWon = getRating().getTipsWon();
            Long overallTipCount = getRating().getOverallTipCount();
            Long overallMatchCount = getRating().getOverallMatchCount();
            double wonLost = 100 * ((1.01 + tipsWon)/(1.01 + tipsLost));
            double activity = (tipsWon * 0.05) - (tipsLost * 0.1) + (overallTipCount * 0.02) + (overallMatchCount * 0.01);
            double coefficient = getRating().getOverallCoefficient();
            double overallScore = wonLost + activity + coefficient;
            getRating().setOverallScore(overallScore);
        }
        return getRating().getOverallScore();
    }
}
