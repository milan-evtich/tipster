package com.milan.tipster.dto;

import lombok.Data;

@Data
public class RatingDto {

    private Long tipsWon;
    private Long tipsLost;
    private Double overallScore;
//    private Long overallRating;
    private Double overallCoefficient;

}
