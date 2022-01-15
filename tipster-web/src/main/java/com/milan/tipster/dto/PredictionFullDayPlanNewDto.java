package com.milan.tipster.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PredictionFullDayPlanNewDto {

    private String day;
    private List<ShortTipDto> singlesMainPlan;
    private List<ShortTipDto> singlesAdditionalPlan;
    private List<ShortTipDto> combos;
    private List<PredictionTipDto> topTips;

}
