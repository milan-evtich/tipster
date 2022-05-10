package com.milan.tipster.controller;

import com.milan.tipster.dto.PredictionFullDayPlanDto;
import com.milan.tipster.dto.PredictionFullDayPlanNewDto;
import com.milan.tipster.dto.PredictionTipDto;
import com.milan.tipster.dto.ShortTipDto;
import com.milan.tipster.mapper.TipToPredictionOrikaMapper;
import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.ETipFilter;
import com.milan.tipster.service.TipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequiredArgsConstructor
public class PredictionController {

    private static final int MAX_COMBO_SIZE = 3;
    private static final int MAX_SYSTEM_PLAN = 7;

    private final TipService tipService;
    private final TipToPredictionOrikaMapper tipToPredictionOrikaMapper;

    @GetMapping("/tips/prediction/{top}/hours/{inHours}/minutes/{inMinutes}")
    ResponseEntity<List<PredictionTipDto>> getTipsPredictionForToday(@PathVariable int top,
                                                                     @PathVariable(required = false) Integer inHours,
                                                                     @PathVariable(required = false) Integer inMinutes) {
        Integer hours = Objects.nonNull(inHours) ? inHours : 0;
        Integer minutes = Objects.nonNull(inMinutes) ? inMinutes : 30;
        return ResponseEntity.ok(tipService.getTipsPredictionForToday(top, hours, minutes, ETipFilter.DEFAULT));
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
                                                                       @RequestParam(required = false) ETipFilter tipFilter) {
        ETipFilter filter = Objects.nonNull(tipFilter) ? tipFilter : ETipFilter.ODDS_1_9__2_75_TIPMAN_21_COMP_67;
        List<PredictionTipDto> tipsPredictionForToday = tipService.getTipsPredictionForToday(top, 0, 30, filter);
        List<ShortTipDto> tipsPlan = new ArrayList<>();
        AtomicInteger dayPlanRank = new AtomicInteger(1);
        tipsPredictionForToday.stream()
                .forEach(t -> addToPlansIfSlotEmpty(tipsPlan, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), t, dayPlanRank.getAndIncrement()));
        tipsPlan.sort(Comparator.comparing(ShortTipDto::getPlayedOn));
        return ResponseEntity.ok(PredictionFullDayPlanDto.builder()
                .day(LocalDate.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE))
                .topTips(tipsPredictionForToday)
                .plan(tipsPlan)
                .build());
    }

    @GetMapping("/tips/prediction/top/{top}/full-day-plan-new")
    ResponseEntity<PredictionFullDayPlanNewDto> getTipsPredictionNewForToday(@PathVariable int top,
                                                                       @RequestParam(required = false) ETipFilter tipFilter) {
        ETipFilter filter = Objects.nonNull(tipFilter) ? tipFilter : ETipFilter.DEFAULT;
        List<PredictionTipDto> tipsPredictionForToday = tipService.getTipsPredictionForToday(top, 0, 1, filter);
        List<ShortTipDto> tipsSingleFirstPlan = new ArrayList<>();
        List<ShortTipDto> tipsSingleSecondPlan = new ArrayList<>();
        List<ShortTipDto> tipsSingleThirdPlan = new ArrayList<>();
        List<ShortTipDto> tipsComboPlan = new ArrayList<>();
        List<ShortTipDto> tipsSystemPlan = new ArrayList<>();
        List<ShortTipDto> tips1pX = new ArrayList<>();
        AtomicInteger dayPlanRank = new AtomicInteger(1);

        tipsPredictionForToday
                .forEach(t -> addToPlansIfSlotEmpty(tipsSingleFirstPlan, tipsSingleSecondPlan, tipsSingleThirdPlan,
                        tipsComboPlan, tipsSystemPlan, t, dayPlanRank.getAndIncrement()));

        tipsPredictionForToday
                .stream()
                .filter(t -> t.getPick().equals(EPick.SPOT_X))
                .forEach(t -> addTipToPlan(tips1pX, t, dayPlanRank.getAndIncrement(), 45));

        tipsSingleFirstPlan.sort(Comparator.comparing(ShortTipDto::getPlayedOn));
        tipsSingleSecondPlan.sort(Comparator.comparing(ShortTipDto::getPlayedOn));
        tipsSingleThirdPlan.sort(Comparator.comparing(ShortTipDto::getPlayedOn));
        tipsComboPlan.sort(Comparator.comparing(ShortTipDto::getPlayedOn));
        tipsSystemPlan.sort(Comparator.comparing(ShortTipDto::getPlayedOn));
        tips1pX.sort(Comparator.comparing(ShortTipDto::getPlayedOn));

        return ResponseEntity.ok(PredictionFullDayPlanNewDto
                .builder()
                .day(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))

                .singlesFirstNotes(makeNotesFromDtoList(tipsSingleFirstPlan))
                .singlesSecondNotes(makeNotesFromDtoList(tipsSingleSecondPlan))
                .singlesThirdNotes(makeNotesFromDtoList(tipsSingleThirdPlan))
                .p1xNotes(makeNotesFromDtoList(tips1pX))
                .comboNotes(makeNotesFromDtoList(tipsComboPlan))
                .systemNotes(makeNotesFromDtoList(tipsSystemPlan))

                .singlesFirstPlan(tipsSingleFirstPlan)
                .singlesSecondPlan(tipsSingleSecondPlan)
                .singlesThirdPlan(tipsSingleThirdPlan)
                .plan1pX(tips1pX)
                .combos(tipsComboPlan)
                .system(tipsSystemPlan)

                .build());
    }

    private List<String> makeNotesFromDtoList(List<ShortTipDto> shortTipDtoList) {
        if (CollectionUtils.isEmpty(shortTipDtoList)) {
            return Collections.emptyList();
        }
        return shortTipDtoList.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    private void addToPlansIfSlotEmpty(List<ShortTipDto> tipsFirstPlan,
                                       List<ShortTipDto> tipsSecondPlan,
                                       List<ShortTipDto> tipsThirdPlan,
                                       List<ShortTipDto> comboPlan,
                                       List<ShortTipDto> systemPlan,
                                       PredictionTipDto tip, int dayPlanRank) {
        if (tip.getPick().equals(EPick.SPOT_X)) {
            // X ignored
            return;
        }
        boolean addedToFirstPlan = addTipToPlan(tipsFirstPlan, tip, dayPlanRank, 100);
        if (!addedToFirstPlan) {
            boolean addedToSecondPlan = addTipToPlan(tipsSecondPlan, tip, dayPlanRank, 100);
            if (!addedToSecondPlan) {
                boolean addedToThirdPlan = addTipToPlan(tipsThirdPlan, tip, dayPlanRank, 100);
                if (!addedToThirdPlan) {
                    ShortTipDto shortTipDto = tipToPredictionOrikaMapper.map(tip, ShortTipDto.class);
                    shortTipDto.setDayPlanRank(dayPlanRank);
                    if (((tip.getPick().equals(EPick.SPOT_2X) || tip.getPick().equals(EPick.SPOT_1X)) && tip.getOdds() > 1.4)
                            || ((tip.getPick().equals(EPick.SPOT_DNB_1) || tip.getPick().equals(EPick.SPOT_DNB_2)) && (tip.getOdds() > 1.65))
                            || (tip.getOdds() >= 1.9)) {
                        if (systemPlan.size() < MAX_SYSTEM_PLAN) {
                            systemPlan.add(shortTipDto);
                        }
                    } else {
                        if(comboPlan.size() < MAX_COMBO_SIZE) {
                            comboPlan.add(shortTipDto);
                        }
                    }
                }
            }
        }
    }

    private boolean addTipToPlan(List<ShortTipDto> tipsMainPlan, PredictionTipDto tip, int dayPlanRank, int timeBound) {
        if ((tip.getPick().equals(EPick.SPOT_1 ) && tip.getOdds() < 1.9)
                || (tip.getPick().equals(EPick.SPOT_1 ) && tip.getOdds() > 2.5)
                || (tip.getPick().equals(EPick.SPOT_2 ) && tip.getOdds() < 1.9)
                || (tip.getPick().equals(EPick.SPOT_2 ) && tip.getOdds() > 2.5)
                || (tip.getPick().equals(EPick.SPOT_1X ) && tip.getOdds() < 1.2)
                || (tip.getPick().equals(EPick.SPOT_1X ) && tip.getOdds() > 1.55)
                || (tip.getPick().equals(EPick.SPOT_2X ) && tip.getOdds() < 1.2)
                || (tip.getPick().equals(EPick.SPOT_2X ) && tip.getOdds() > 1.55)
                || (tip.getPick().equals(EPick.SPOT_DNB_1 ) && tip.getOdds() < 1.4)
                || (tip.getPick().equals(EPick.SPOT_DNB_1 ) && tip.getOdds() > 1.9)
                || (tip.getPick().equals(EPick.SPOT_DNB_2 ) && tip.getOdds() < 1.4)
                || (tip.getPick().equals(EPick.SPOT_DNB_2 ) && tip.getOdds() > 1.9)
        ) {
            return false;
        }
        LocalDateTime tipGameStart = tip.getGame().getPlayedOn();
        AtomicBoolean shouldAddToPlan = new AtomicBoolean(true);
        tipsMainPlan.forEach(t -> {
            if (tipGameStart.isAfter(t.getPlayedOn().minusMinutes(timeBound))
                    && tipGameStart.isBefore(t.getPlayedOn().plusMinutes(timeBound))) {
                shouldAddToPlan.set(false);
            }
        });
        if (shouldAddToPlan.get()) {
            try {
                ShortTipDto shortTipDto = tipToPredictionOrikaMapper.map(tip, ShortTipDto.class);
                shortTipDto.setDayPlanRank(dayPlanRank);
                tipsMainPlan.add(shortTipDto);
                return true;
            } catch (Exception e) {
                log.error("Couldn't add tip {} to tipsMainPlan", tip);
                return false;
            }
        } else {
            return false;
        }
    }
    }
