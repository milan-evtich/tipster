package com.milan.tipster.model;

import com.milan.tipster.model.enums.ECompetitionType;
import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.ESeason;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Competition {

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
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "team_competition",
            joinColumns = @JoinColumn(name = "competitionId", referencedColumnName = "competitionId"),
            inverseJoinColumns = @JoinColumn(name = "teamId", referencedColumnName = "teamId"))
    private Set<Team> teams = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tipman_competition",
            joinColumns = @JoinColumn(name = "competitionId", referencedColumnName = "competitionId"),
            inverseJoinColumns = @JoinColumn(name = "tipmanId", referencedColumnName = "tipmanId"))
    private Set<Tipman> tipmans = new HashSet<>();


    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(
            mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true
    )
    private Set<TipmanCompetitionRating> tipmanCompetitionRatings = new HashSet<>();

}
