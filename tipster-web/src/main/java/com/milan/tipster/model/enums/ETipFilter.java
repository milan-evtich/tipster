package com.milan.tipster.model.enums;

import com.milan.tipster.model.Tip;

import java.util.function.Predicate;

public enum ETipFilter {
    DEFAULT, // ODDS_1_9__2_5_TIPMAN_16_COMP_35
    ODDS_1_9__2_5_TIPMAN_16_COMP_35,
    ODDS_1_5__3_9_TIPMAN_17_COMP_43;

    public static Predicate<Tip> validTipsV1 = tip ->
            tip.getOdds() > 1.89 && tip.getOdds() < 2.51
                    && tip.getTipman().getRank() <= 16 &&
                    tip.getGame().getCompetition().getRank() <= 35;

    public static Predicate<Tip> validTipsV2 = tip ->
            tip.getOdds() > 1.49 && tip.getOdds() < 3.9
                    && tip.getTipman().getRank() <= 17 &&
                    tip.getGame().getCompetition().getRank() <= 43;
}
