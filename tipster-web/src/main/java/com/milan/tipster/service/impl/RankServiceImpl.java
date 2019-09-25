package com.milan.tipster.service.impl;

import com.milan.tipster.model.Rankable;
import com.milan.tipster.service.RankService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RankServiceImpl implements RankService {

    @Override
    public Long getRankValue(Rankable rankable) {

        Integer maxCount = rankable.getMaxCount();
        Map<Long, Long> rankTableMap = rankable.getRankTableMap();
        Long rank = rankable.getRank();
        if (rank != null) {
            for (int i = 0; i < maxCount; i++) {
                if (rankTableMap.containsKey(rank)) {
                    return rankTableMap.get(rank);
                } else {
                    rank++;
                }
            }
        }
        return 1L; // default
    }
}
