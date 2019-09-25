package com.milan.tipster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@ToString
@AllArgsConstructor
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "getTipmanCompetitionRatingByTipmanIdAndCompetitionId",
                query = "SELECT tcr " +
                        "FROM TipmanCompetitionRating tcr " +
                        "WHERE tcr.tipman.tipmanId = ?1 " +
                        "AND tcr.competition.competitionId = ?2",
                resultClass=TipmanCompetitionRating.class
        )
})
public class TipmanCompetitionRating implements Serializable {

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


    public TipmanCompetitionRating(Competition competition, Rating rating) {
        this.competition = competition;
        this.rating = rating;
    }

}
