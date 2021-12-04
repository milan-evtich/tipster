package com.milan.tipster.dto;

import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.ETipStatus;
import com.milan.tipster.model.enums.ETipType;
import lombok.Data;

@Data
public class PredictionTipDto {

    private EPick pick;
    private Double odds;
    private Double score;
    private TipmanCompetitionDto tipmanCompetition;
//    private ETipType type;
    private GameDto game;
    private TipmanDto tipman;
    private Long tipId;

}
