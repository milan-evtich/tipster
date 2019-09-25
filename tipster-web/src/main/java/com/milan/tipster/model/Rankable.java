package com.milan.tipster.model;

import java.util.Map;

public interface Rankable {

    Integer getMaxCount();

    Long getRank();

    Map<Long, Long> getRankTableMap();
}
