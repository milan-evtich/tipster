package com.milan.tipster.model.enums;

public enum ESystemType {

    S9_5_FROM_9(126),
    S9_6_FROM_9(84),
    S9_7_FROM_9(36),
    S9_8_FROM_9(9),
    S9_9_FROM_9(1),
    S8_4_FROM_8(70),
    S8_5_FROM_8(56),
    S8_6_FROM_8(28),
    S8_7_FROM_8(8),
    S8_8_FROM_8(1),
    S7_4_FROM_7(35),
    S7_5_FROM_7(21),
    S7_6_FROM_7(7),
    S7_7_FROM_7(1),
    S6_3_FROM_6(20),
    S6_4_FROM_6(15),
    S6_5_FROM_6(6),
    S6_6_FROM_6(1),
    S5_3_FROM_5(10),
    S5_4_FROM_5(5),
    S5_5_FROM_5(1),
    S4_3_FROM_4(4),
    S4_4_FROM_4(1),
    S3_2_FROM_3(3),
    S3_3_FROM_3(1);

    private final Integer combinations;

    ESystemType(Integer combinations) {
        this.combinations = combinations;
    }

    public Integer getCombinations() {
        return this.combinations;
    }


}
