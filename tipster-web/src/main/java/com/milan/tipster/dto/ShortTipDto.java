package com.milan.tipster.dto;

import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.util.DateTimeUtils;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShortTipDto {
    public static final String BASIC_STACK = "1000";
    private LocalDateTime playedOn;
    private String nameGr;
    private String link;
    private Double odds;
    private Double score;
    private EPick pick;
    private boolean hotMatch;
    private Integer dayPlanRank;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append(DateTimeUtils.format(playedOn.toLocalTime()))
                .append(" ")
                .append(nameGr)
                .append(" ")
                .append(pick.ru())
                .append(" ")
                .append(BASIC_STACK)
                .append(" ")
                .append(odds)
                .append(" ")
                .toString();
    }
}
