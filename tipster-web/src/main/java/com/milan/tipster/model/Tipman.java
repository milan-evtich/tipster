package com.milan.tipster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Tipman implements Rankable, Rateable {

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tipmanId;

    private String fullName;
    @Builder.Default
    private Double score = 0D;
    @Builder.Default
    private Long rank = 0L;

    @Embedded
    @Builder.Default
    private Rating rating = new Rating();

    @ManyToOne
    @JoinColumn(name = "sourceId")
    private Source source;


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnoreProperties({"tipmans"})
    @ManyToMany(mappedBy = "tipmans", fetch = FetchType.EAGER)
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

    @Override
    public void rate() {
        log.info("Tipman {} rated {}", tipmanId, rateRateable());
    }

}
