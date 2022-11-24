package com.milan.tipster.dto;

import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.ETipStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TipSDto {
    private LocalDateTime playedOn;
    private Double odds;
    private Double score;
    private EPick pick;
    private ETipStatus status;
}
