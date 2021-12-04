package com.milan.tipster.model.enums;

public enum EPick implements Scored {

    // SPOT (Главный исход матча)
    SPOT_1(1),
    SPOT_2(0.7),
    SPOT_X(0.2),
    SPOT_DNB_1(0.6),
    SPOT_DNB_2(0.5),
    SPOT_1X(0.4),
    SPOT_2X(0.3),
    NOBET(0),
    UNKNOWN(0) // Неопределенный
    , TODO(0) // TODO SHOULD BE REMOVED
    ;

    public final double score;

    EPick(double score) {
        this.score = score;
    }

    @Override
    public double score() {
        return score;
    }
}
