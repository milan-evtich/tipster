package com.milan.tipster.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PredictionFullDayPlanNewDto {

    private String day;
    private List<String> singlesFirstNotes;
    private List<String> singlesSecondNotes;
    private List<String> singlesThirdNotes;
    private List<String> p1xNotes;
    private List<String> comboNotes;
    private List<String> systemNotes;

    private List<ShortTipDto> singlesFirstPlan;
    private List<ShortTipDto> singlesSecondPlan;
    private List<ShortTipDto> singlesThirdPlan;
    private List<ShortTipDto> plan1pX;
    private List<ShortTipDto> combos;
    private List<ShortTipDto> system;
//    private List<PredictionTipDto> topTips;

}
