package com.milan.tipster.model;

import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.ETipStatus;
import com.milan.tipster.model.enums.ETipType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;

@SqlResultSetMapping(name="updateTipResult", columns = { @ColumnResult(name = "count")})

@NamedNativeQueries({
        @NamedNativeQuery(
                name    =   "updateTipPick",
                query   =   "UPDATE tip SET pick = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        ),
        @NamedNativeQuery(
                name    =   "updateTipDnb",
                query   =   "UPDATE tip SET dnb = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        ),
        @NamedNativeQuery(
                name    =   "updateTipDoubleChance",
                query   =   "UPDATE tip SET doubleChance = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        ),
        @NamedNativeQuery(
                name    =   "updateTipOdds",
                query   =   "UPDATE tip SET odds = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        ),
        @NamedNativeQuery(
                name    =   "updateTipHotMatch",
                query   =   "UPDATE tip SET hot_match = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        ),
        @NamedNativeQuery(
                name    =   "updateTipStatus",
                query   =   "UPDATE tip SET status = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        ),
        @NamedNativeQuery(
                name    =   "updateTipType",
                query   =   "UPDATE tip SET type = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        ),
        @NamedNativeQuery(
                name    =   "updateTipTipman",
                query   =   "UPDATE tip SET tipman_id = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        ),
        @NamedNativeQuery(
                name    =   "updateTipGame",
                query   =   "UPDATE tip SET game_id = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        ),
        @NamedNativeQuery(
                name    =   "updateTipText",
                query   =   "UPDATE tip SET text = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        ),
        @NamedNativeQuery(
                name    =   "updateTipStrong",
                query   =   "UPDATE tip SET strong = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        ),
        @NamedNativeQuery(
                name    =   "updateTipFetchStatus",
                query   =   "UPDATE tip SET fetch_status = ?1 WHERE tip_id = ?2",
                resultSetMapping = "updateTipResult"
        )
})

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Tip {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tipId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ETipStatus status = ETipStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private ETipType type;

    @Enumerated(EnumType.STRING)
    private EPick pick;

    @Embedded
    @Builder.Default
    private RatedFlag ratedFlag = new RatedFlag();

    @ManyToOne
    @JoinColumn(name = "gameId")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "tipmanId")
    private Tipman tipman;

    @Enumerated(EnumType.STRING)
    private EFetchStatus fetchStatus;

    private Boolean hotMatch;

    private Double odds;

    private String bookie;

    @Builder.Default
    private boolean dnb = false;

    @Builder.Default
    private boolean doubleChance = false;

    private String strong;

    private String text; // maybe is not needed

    @Builder.Default
    private Double score = 0D;
}
