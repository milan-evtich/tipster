package com.milan.tipster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
@Entity
@ToString
@NoArgsConstructor
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "getTipmanCompetitionRatingByTipmanIdAndCompetitionId",
                query = "SELECT * " +
                        "FROM tipman_competition_rating tcr " +
                        "WHERE tcr.tipman_tipman_id = :tipmanId " +
                        "AND tcr.competition_competition_id = :competitionId",
                resultClass=TipmanCompetitionRating.class
        ),
        @NamedNativeQuery(
                name = "getAllTipmanCompetitionRating",
                query = "SELECT * " +
                        "FROM tipman_competition_rating tcr ",
                resultClass=TipmanCompetitionRating.class
        )
})
public class TipmanCompetitionRating implements Serializable, Rankable, Rateable {

    private static final Map<Long, Long> rankTableMap;
    private static final Integer maxCount = 30;

    static {
        // 28 tipmans
        Map<Long, Long> aMap = new HashMap<>();
        aMap.put(1L, 10L);  // rank 1 10%
        aMap.put(2L, 10L);  // 2
        aMap.put(3L, 9L);   // 3
        aMap.put(4L, 9L);   // 4
        aMap.put(5L, 8L);   // 5
        aMap.put(6L, 8L);   // 6
        aMap.put(7L, 7L);   // 7
        aMap.put(8L, 7L);   // 8
        aMap.put(9L, 7L);   // 9
        aMap.put(10L, 6L);  // 10
        aMap.put(11L, 6L);  // 11
        aMap.put(12L, 6L);  // 12
        aMap.put(13L, 5L);  // 13
        aMap.put(14L, 5L);  // 14
        aMap.put(15L, 5L);  // 15
        aMap.put(16L, 4L);  // 16
        aMap.put(17L, 4L);  // 17
        aMap.put(18L, 4L);  // 18
        aMap.put(19L, 3L);  // 19
        aMap.put(20L, 3L);  // 20
        aMap.put(21L, 3L);  // 21
        aMap.put(22L, 3L);  // 22
        aMap.put(23L, 2L);  // 23
        aMap.put(24L, 2L);  // 24
        aMap.put(25L, 2L);  // 25
        aMap.put(26L, 2L);  // 26
        aMap.put(27L, 1L);  // 27
        aMap.put(28L, 1L);  // 28
        aMap.put(29L, 1L);  // 29
        aMap.put(30L, 1L);  // 30
        //        aMap.put(100L, 1L); // 26-*
        rankTableMap = Collections.unmodifiableMap(aMap);
    }


    @Id
    @ManyToOne
    @JoinColumn
    private Tipman tipman;

    @Id
    @ManyToOne
    @JoinColumn
    private Competition competition;

    @Embedded
    private Rating rating;

    private Long rank = 0L;

    public TipmanCompetitionRating(Competition competition, Rating rating) {
        this.competition = competition;
        this.rating = rating;
    }

    @Override
    public Integer getMaxCount() {
        return maxCount;
    }

    @Override
    public Long getRank() {
        return rank;
    }

    @Override
    public Map<Long, Long> getRankTableMap() {
        return rankTableMap;
    }

    @Override
    public void rate() {
        log.info("TipmanCompetitionRating Tipman {} Competition {} rated {}",tipman.getTipmanId(),
                competition.getCompetitionId(), rateRateable());
    }

}
