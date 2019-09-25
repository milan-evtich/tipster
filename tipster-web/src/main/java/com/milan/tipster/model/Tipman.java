package com.milan.tipster.model;

import lombok.*;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Entity
@Builder
@AllArgsConstructor
public class Tipman implements Rankable {

    private static final Map<Long, Long> rankTableMap;
    private static final Integer maxCount = 30;

    static {
        // 28 tipmans
        Map<Long, Long> aMap = new HashMap<>();
        aMap.put(2L, 10L);  // 1-2
        aMap.put(4L, 9L);   // 3-4
        aMap.put(6L, 8L);   // 5-6
        aMap.put(9L, 7L);   // 7-9
        aMap.put(12L, 6L);  // 10-12
        aMap.put(15L, 5L);  // 13-15
        aMap.put(18L, 4L);  // 16-18
        aMap.put(22L, 3L);  // 19-22
        aMap.put(26L, 2L);  // 23-26
        aMap.put(100L, 1L); // 26-*
        rankTableMap = Collections.unmodifiableMap(aMap);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tipmanId;

    private String fullName;
    private Double score;
    private Long rank;

    @Embedded
    private Rating rating;

    @ManyToOne
    @JoinColumn(name = "sourceId")
    private Source source;


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "tipmans")
    private Set<Competition> competitions = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "tipman", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER
    )
    private Set<TipmanCompetitionRating> tipmanCompetitionRatings = new HashSet<>();

    public Tipman(String fullName, TipmanCompetitionRating... tipmanCompetitionRatings) {
        this.fullName = fullName;
        for (TipmanCompetitionRating tipmanCompetitionRating : tipmanCompetitionRatings) {
            tipmanCompetitionRating.setTipman(this);
        }
        this.tipmanCompetitionRatings = Stream.of(tipmanCompetitionRatings).collect(Collectors.toSet());
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

}
