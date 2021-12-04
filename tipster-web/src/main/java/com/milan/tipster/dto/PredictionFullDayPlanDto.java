package com.milan.tipster.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PredictionFullDayPlanDto {
    private String day;
    private List<ShortTipDto> plan;
    private List<PredictionTipDto> topTips;

}
