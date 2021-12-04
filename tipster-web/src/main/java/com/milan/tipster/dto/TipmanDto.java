package com.milan.tipster.dto;

import lombok.Data;

@Data
public class TipmanDto {

    private Long rank;
    private RatingDto rating;
    private String fullName;
    private Double score;
    private Long tipmanId;

}
