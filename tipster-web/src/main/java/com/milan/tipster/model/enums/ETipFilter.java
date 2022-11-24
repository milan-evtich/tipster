package com.milan.tipster.model.enums;

import com.milan.tipster.model.Tip;

import java.util.function.Predicate;

public enum ETipFilter {
    DEFAULT,
    ODDS_1_9__2_75_TIPMAN_21_COMP_67,
    ODDS_1_5__3_9_TIPMAN_23_COMP_72;

    private static final int MINIMAL_SCORE = 3900;

    public static Predicate<Tip> defaultFilter = tip ->
            ((
                    ((tip.getPick().equals(EPick.SPOT_1X) || tip.getPick().equals(EPick.SPOT_2X)) && tip.getOdds() >= 1.2 && tip.getOdds() < 2.5)
                            || ((tip.getPick().equals(EPick.SPOT_DNB_1) || tip.getPick().equals(EPick.SPOT_DNB_2)) && (tip.getOdds() >= 1.4 && tip.getOdds() <= 2.9))
                            || (tip.getPick().equals(EPick.SPOT_X) && tip.getOdds() > 2.8)
                            || (tip.getOdds() >= 1.5 && tip.getOdds() <= 3.5)
            ))
                    && tip.getScore() >= MINIMAL_SCORE;

    public static Predicate<Tip> validTipsV1 = tip ->
                    ((
                            ((tip.getPick().equals(EPick.SPOT_1X) || tip.getPick().equals(EPick.SPOT_2X)) && tip.getOdds() >= 1.5 && tip.getOdds() < 2.35)
                                    || ((tip.getPick().equals(EPick.SPOT_DNB_1) || tip.getPick().equals(EPick.SPOT_DNB_2)) && (tip.getOdds() >= 1.7 && tip.getOdds() <= 2.5))
                                    || (tip.getOdds() >= 1.9 && tip.getOdds() <= 2.75)
                    ))
                    && tip.getTipman().getRank() <= 21
                    && tip.getGame().getCompetition().getRank() <= 67;

    public static Predicate<Tip> validTipsV2 = tip ->
                ((
                        ((tip.getPick().equals(EPick.SPOT_1X) || tip.getPick().equals(EPick.SPOT_2X)) && tip.getOdds() >= 1.3 && tip.getOdds() < 2.5)
                                || ((tip.getPick().equals(EPick.SPOT_DNB_1) || tip.getPick().equals(EPick.SPOT_DNB_2)) && (tip.getOdds() >= 1.4 && tip.getOdds() <= 3))
                                || (tip.getOdds() >= 1.5 && tip.getOdds() <= 3.9)
                ))
                    && tip.getTipman().getRank() <= 23
                    && tip.getGame().getCompetition().getRank() <= 72;
}
