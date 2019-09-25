package com.milan.tipster.model.enums;

public enum ETipType {

    SPOT("spot"), // 1X2
    GOAL("goal"),
    SPECIAL("special");

    private final String type;

    ETipType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
