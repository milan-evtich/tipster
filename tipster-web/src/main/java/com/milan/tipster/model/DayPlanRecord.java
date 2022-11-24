package com.milan.tipster.model;

import com.milan.tipster.dto.TipSDto;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@Slf4j
public class DayPlanRecord {

    public DayPlanRecord(Double money, Map<Integer, List<TipSDto>> singlesPlansMap) {
        this.money = money;
        this.singlesPlansMap = singlesPlansMap;
    }

    private Double money;
    private Map<Integer, List<TipSDto>> singlesPlansMap;
    @Builder.Default
    private List<TipSDto> plan1pX = new ArrayList<>();
    @Builder.Default
    private List<TipSDto> combos = new ArrayList<>();
    @Builder.Default
    private List<TipSDto> system = new ArrayList<>();

    public List<TipSDto> singlePlanList(int i) {
        if (Objects.isNull(singlesPlansMap)) {
            log.error("SinglePlansMap shouldn't be null when getPlan(i) is called");
            return null;
        }
        return singlesPlansMap.get(i);
    }
}
