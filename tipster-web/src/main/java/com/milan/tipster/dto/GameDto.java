package com.milan.tipster.dto;

import com.milan.tipster.model.Competition;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameDto {

    private LocalDateTime playedOn;
    private String nameGr;
    private CompetitionDto competition;
    private Long gameId;
    private String link;

}
