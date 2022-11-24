package com.milan.tipster.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StrategyScoreDto {

    private Long profit;
    private Integer evaluationDays;
    private PredictionStrategyConfigDto predictionStrategyConfig;

}
