package com.milan.tipster.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PredictionFullDayPlanNewDto {

    private String day;
    private List<ShortTipDto> singlesFirstPlan;
    private List<ShortTipDto> singlesSecondPlan;
    private List<ShortTipDto> singlesThirdPlan;
    private List<ShortTipDto> combos;
    private List<ShortTipDto> system;
    private List<PredictionTipDto> topTips;

}
