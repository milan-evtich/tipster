package com.milan.tipster.model.enums;

import com.milan.tipster.model.Tip;

import java.util.function.Predicate;

public enum ETipFilter {
    DEFAULT, // ODDS_1_8__2_75_TIPMAN_17_COMP_61
    ODDS_1_8__2_75_TIPMAN_17_COMP_61,
    ODDS_1_5__3_9_TIPMAN_21_COMP_75;

    public static Predicate<Tip> validTipsV1 = tip ->
                    ((
                            tip.isDoubleChance() && tip.getOdds() >= 1.4 && tip.getOdds() < 2.35)
                            || (tip.isDnb() && tip.getOdds() >= 1.6 && tip.getOdds() <= 2.55)
                            || (tip.getOdds() >= 1.8 && tip.getOdds() <= 2.75
                    ))
                    && tip.getTipman().getRank() <= 17
                    && tip.getGame().getCompetition().getRank() <= 61;

    public static Predicate<Tip> validTipsV2 = tip ->
                ((
                        tip.isDoubleChance() && tip.getOdds() >= 1.3 && tip.getOdds() < 2.5)
                        || (tip.isDnb() && tip.getOdds() >= 1.4 && tip.getOdds() <= 3)
                        || (tip.getOdds() >= 1.5 && tip.getOdds() <= 3.9
                ))
                    && tip.getTipman().getRank() <= 21
                    && tip.getGame().getCompetition().getRank() <= 75;
}
