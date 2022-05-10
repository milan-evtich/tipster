package com.milan.tipster.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class ScoreUtils {

    public static boolean isBetween(double x, double lower, double upper) {
        return lower < x && x <= upper;
    }

    public static double oddsDifferenceScore(Double oldOdds, Double newOdds) {
        if (Objects.isNull(oldOdds) || oldOdds.equals(0D) || oldOdds.equals(-1D) || Objects.isNull(newOdds)
                || newOdds.equals(0D) || newOdds.equals(-1D) ) {
            return 0D;
        }
        double dif = oldOdds - newOdds;
        if(-1D < dif && dif < 1D) {
            return 25D * dif;
        }
        return 0D;
    }

    public static double oddsScore(Double odds) {

//        Objects.requireNonNull(odds);
        if (Objects.isNull(odds)) {
            // не учитываем типы без коэффициентов
            return -10;
        }
        if (odds <= 1) {
            log.warn("Odds should be more than 1");
            return 0;
        }
        if (isBetween(odds, 1, 1.3)) {
            return 0.1;
        } else if (isBetween(odds, 1.3, 1.6)) {
            return 0.3;
        } else if (isBetween(odds, 1.6, 1.8)) {
            return 0.6;
        } else if (isBetween(odds, 1.8, 1.9)) {
            return 0.8;
        } else if (isBetween(odds, 1.9, 2)) {
            return 1;
        } else if (isBetween(odds, 2, 2.2)) {
            return 1.5;
        } else if (isBetween(odds, 2.2, 2.4)) {
            return 1;
        } else if (isBetween(odds, 2.4, 2.6)) {
            return 0.9;
        } else if (isBetween(odds, 2.6, 3)) {
            return 0.8;
        } else if (isBetween(odds, 3, 4)) {
            return 0.6;
        } else if (odds > 4) {
            return 0.5;
        }
        // shouldn't happen
        return 0;
    }

}
