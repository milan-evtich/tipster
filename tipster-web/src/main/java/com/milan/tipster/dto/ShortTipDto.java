package com.milan.tipster.dto;

import com.milan.tipster.model.enums.EPick;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortTipDto {
    private LocalDateTime playedOn;
    private String nameGr;
    private String link;
    private Double odds;
    private Double score;
    private EPick pick;
}
