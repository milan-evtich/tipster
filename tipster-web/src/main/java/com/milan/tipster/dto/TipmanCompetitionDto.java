package com.milan.tipster.dto;

import lombok.Data;

@Data
public class TipmanCompetitionDto {

    private Long rank;
    private RatingDto rating;
    private Long competitionId;
    private Long tipmanId;

}
