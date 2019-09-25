package com.milan.tipster.model;

import com.milan.tipster.model.enums.EScoreType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Score {

    private Integer homeGoals;
    private Integer awayGoals;

    @Enumerated(EnumType.STRING)
    private EScoreType scoreType;
}
