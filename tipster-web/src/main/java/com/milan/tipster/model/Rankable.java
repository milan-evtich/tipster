package com.milan.tipster.model;

import java.util.Map;

public interface Rankable {

    Integer getMaxCount();

    Long getRank();

    void setRank(long rank);

    Map<Long, Long> getRankTableMap();

    void rate();
}
