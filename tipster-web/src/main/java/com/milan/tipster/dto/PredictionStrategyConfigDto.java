package com.milan.tipster.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class PredictionStrategyConfigDto {

    private String strategyName;
    private @NotNull LocalDate startDate;
    private @NotNull LocalDate endDate;
    private Double scoreMin;
    private Integer startMoney;
    private StrategyConfig singleStrategy;
    private StrategyConfig comboStrategy;
    private StrategyConfig p1XStrategy;
    private StrategyConfig drawStrategy;
    private StrategyConfig systemStrategy;

}
