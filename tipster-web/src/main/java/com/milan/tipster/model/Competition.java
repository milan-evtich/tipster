package com.milan.tipster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.milan.tipster.model.enums.ECompetitionType;
import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.ESeason;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Slf4j
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Competition implements Rankable, Rateable {

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
    private Long competitionId;

    private String code;
    private String name;
    private String nameGr;
    private Long rank; // ?
    private String label;

    @Enumerated(EnumType.STRING)
    private ESeason season;

    @Enumerated(EnumType.STRING)
    private ECompetitionType type;

    @Enumerated(EnumType.STRING)
    private EFetchStatus fetchStatus;

    @ManyToOne
    @JoinColumn(name = "country")
    private Country country;

    @Embedded
    private Rating rating;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "team_competition",
            joinColumns = @JoinColumn(name = "competitionId", referencedColumnName = "competitionId"),
            inverseJoinColumns = @JoinColumn(name = "teamId", referencedColumnName = "teamId"))
    private Set<Team> teams = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"competitions"})
    @JoinTable(name = "tipman_competition",
            joinColumns = @JoinColumn(name = "competitionId", referencedColumnName = "competitionId"),
            inverseJoinColumns = @JoinColumn(name = "tipmanId", referencedColumnName = "tipmanId"))
    private Set<Tipman> tipmans = new HashSet<>();


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
        log.info("Competition {} rated {}", competitionId, rateRateable());
    }


/*
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true
    )
    private Set<TipmanCompetitionRating> tipmanCompetitionRatings = new HashSet<>();
*/

}
