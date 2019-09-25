package com.milan.tipster.model;

import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.EScoreType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@SqlResultSetMapping(name="updateGameResult", columns = { @ColumnResult(name = "count")})

@NamedNativeQueries({
        @NamedNativeQuery(
                name    =   "updateGamePlayedOnDate",
                query   =   "UPDATE game SET played_on = ?1 WHERE game_id = ?2",
                resultSetMapping = "updateGameResult"
        ),
        @NamedNativeQuery(
                name    =   "updateGameNameGr",
                query   =   "UPDATE game SET name_gr = ?1 WHERE game_id = ?2",
                resultSetMapping = "updateGameResult"
        ),
        @NamedNativeQuery(
                name    =   "updateGameFetchStatus",
                query   =   "UPDATE game SET fetch_status = ?1 WHERE game_id = ?2",
                resultSetMapping = "updateGameResult"
        ),
        @NamedNativeQuery(
                name    =   "updateGameScore",
                query   =   "UPDATE game SET score_type = ?1, home_goals = ?2, away_goals = ?3 WHERE game_id = ?4",
                resultSetMapping = "updateGameResult"
        ),
        @NamedNativeQuery(
                name = "getGamesByPick",
                query = "select * from game where game_id in ( select game_id from tip where pick = ?1)" ,
                resultClass=Game.class
        )
})

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long gameId;

    private String link;

    private LocalDateTime playedOn;

    // Когда тип был выпущен в latest-analysis page
    private LocalDateTime published;

    // unique; example: mpinasional-sesar-vagiecho-16-2-19-22-30
    private String code;

    // Not unique; example: ΜΠΙΝΑΣΙΟΝΑΛ-ΣΕΣΑΡ ΒΑΓΙΕΧΟ
    private String nameGr;

    @Enumerated(EnumType.STRING)
    private EFetchStatus fetchStatus;

    @Embedded
    private Score score;

    @ManyToOne
    @JoinColumn(name = "home_team")
    private Team homeTeam;

    @ManyToOne
    @JoinColumn(name = "away_team")
    private Team awayTeam;

    @ManyToOne
    @JoinColumn(name="competitionId")
    @ToString.Exclude
    private Competition competition;

}
