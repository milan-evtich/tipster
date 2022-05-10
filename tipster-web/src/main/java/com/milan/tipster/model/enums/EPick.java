package com.milan.tipster.model.enums;

public enum EPick implements Scored {

    // SPOT (Главный исход матча)
    SPOT_1(1, "1"),
    SPOT_2(0.9, "2"),
    SPOT_X(0.1, "X"),
    SPOT_DNB_1(0.2, "1(0)"),
    SPOT_DNB_2(0.15, "2(0)"),
    SPOT_1X(0.15, "X1"),
    SPOT_2X(0.125, "X2"),
    NOBET(0, "НОБЕТ"),
    UNKNOWN(0, "НЕПОНЯТНО") // Неопределенный
    , TODO(0, "ТОДО") // TODO SHOULD BE REMOVED
    ;

    public final double score;
    public final String ru;

    EPick(double score, String ru) {
        this.score = score;
        this.ru = ru;
    }

    @Override
    public double score() {
        return score;
    }

    public String ru() {
        return ru;
    }
}
