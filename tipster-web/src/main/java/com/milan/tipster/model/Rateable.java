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
            double wonLost = 175 * ((1.01 + tipsWon)/(1.01 + tipsLost));
            double activity = (tipsWon * 0.1) - (tipsLost * 0.5) + (overallTipCount * 0.01) + (overallMatchCount * 0.005);
            double coefficient = getRating().getOverallCoefficient();
            double overallScore = wonLost + activity + coefficient;
            getRating().setOverallScore(overallScore);
        }
        return getRating().getOverallScore();
    }

    default double rateNewRateable() {
        if (getRating().getOverallTipCount() > 0L) {
            Long tipsLost = getRating().getTipsLost();
            Long tipsWon = getRating().getTipsWon();
            Long overallTipCount = getRating().getOverallTipCount();
            Long overallMatchCount = getRating().getOverallMatchCount();
            double wonLost = 175 * ((1.01 + tipsWon)/(1.01 + tipsLost));
            double activity = (tipsWon * 0.1) - (tipsLost * 0.5) + (overallTipCount * 0.01) + (overallMatchCount * 0.005);
            double coefficient = getRating().getOverallCoefficient();
            double coefficientNew = getRating().getNewCoefficient();
            double overallScore = wonLost + activity + coefficientNew + (1.75 * (coefficientNew - coefficient));

            getRating().setOverallRating((long) overallScore);
        }
        return getRating().getOverallRating();
    }
}
