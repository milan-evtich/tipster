package com.milan.tipster.controller;

import com.milan.tipster.dto.PredictionFullDayPlanDto;
import com.milan.tipster.dto.PredictionTipDto;
import com.milan.tipster.dto.ShortTipDto;
import com.milan.tipster.mapper.TipToPredictionOrikaMapper;
import com.milan.tipster.model.Tip;
import com.milan.tipster.service.TipService;
import com.milan.tipster.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@RestController
@RequiredArgsConstructor
public class PredictionController {

    private final TipService tipService;
    private final TipToPredictionOrikaMapper tipToPredictionOrikaMapper;

    @GetMapping("/tips/prediction/{top}/hours/{inHours}/minutes/{inMinutes}")
    ResponseEntity<List<PredictionTipDto>> getTipsPredictionForToday(@PathVariable int top,
                                                                     @PathVariable(required = false) Integer inHours,
                                                                     @PathVariable(required = false) Integer inMinutes) {
        Integer hours = Objects.nonNull(inHours) ? inHours : 0;
        Integer minutes = Objects.nonNull(inMinutes) ? inMinutes : 30;
        return ResponseEntity.ok(tipService.getTipsPredictionForToday(top, hours, minutes));
    }


    @GetMapping("/tips/prediction/{top}/minutes-start-from-now/{minutes}/for-period-in-hours/{period}")
    ResponseEntity<List<PredictionTipDto>> getTipsPredictionForPeriod(@PathVariable int top,
                                                                     @PathVariable(required = false) Integer minutes,
                                                                     @PathVariable(required = false) Integer period) {
        int start = Objects.nonNull(minutes) ? minutes : 0;
        LocalDateTime startDateTime = LocalDateTime.now().plusMinutes(start);
        int end = Objects.nonNull(period) ? period : 4;
        LocalDateTime endDateTime = LocalDateTime.now().plusHours(end);

        return ResponseEntity.ok(tipService.getTipsPredictionForPeriod(top, startDateTime, endDateTime));
    }

    @GetMapping("/tips/prediction/top/{top}/full-day-plan")
    ResponseEntity<PredictionFullDayPlanDto> getTipsPredictionForToday(@PathVariable int top,
                                                                       @PathVariable(required = false) Integer box) {
        List<PredictionTipDto> tipsPredictionForToday = tipService.getTipsPredictionForToday(top, 0, 30);
        List<ShortTipDto> tipsPlan = new ArrayList<>();
        tipsPredictionForToday.forEach(t -> addToPlanIfSlotEmpty(tipsPlan, t));
        tipsPlan.sort(Comparator.comparing(ShortTipDto::getPlayedOn));
        return ResponseEntity.ok(PredictionFullDayPlanDto.builder()
                .day(LocalDate.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE))
                .topTips(tipsPredictionForToday)
                .plan(tipsPlan)
                .build());
    }

    private void addToPlanIfSlotEmpty(List<ShortTipDto> tipsPlan, PredictionTipDto tip) {
        LocalDateTime tipGameStart = tip.getGame().getPlayedOn();
        AtomicBoolean shouldAddToPlan = new AtomicBoolean(true);
        tipsPlan.forEach(t -> {
            if (tipGameStart.isAfter(t.getPlayedOn().minusMinutes(100))
                    && tipGameStart.isBefore(t.getPlayedOn().plusMinutes(100))) {
                shouldAddToPlan.set(false);
            }
        });
        if (shouldAddToPlan.get()) {
            tipsPlan.add(tipToPredictionOrikaMapper.map(tip, ShortTipDto.class));
        }
    }
}
