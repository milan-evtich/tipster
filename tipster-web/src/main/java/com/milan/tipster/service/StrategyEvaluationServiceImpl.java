package com.milan.tipster.service;

import com.milan.tipster.dto.PredictionStrategyConfigDto;
import com.milan.tipster.dto.TipSDto;
import com.milan.tipster.dto.StrategyConfig;
import com.milan.tipster.dto.StrategyScoreDto;
import com.milan.tipster.error.exception.TipsterException;
import com.milan.tipster.mapper.TipToPredictionOrikaMapper;
import com.milan.tipster.model.DayPlanRecord;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.EStrategyPart;
import com.milan.tipster.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StrategyEvaluationServiceImpl implements StrategyEvaluationService {

    private static final Double SPOT_DNB_COEF = 1.375;
    private static final Double SPOT_X1_COEF = 1.63;
    private final TipService tipService;
    private final TipToPredictionOrikaMapper tipToPredictionOrikaMapper;

    @Override
    public StrategyScoreDto evaluate(final PredictionStrategyConfigDto config) {
        if (!config.getStartDate().isBefore(config.getEndDate())) {
            throw new TipsterException("Validation error for PredictionStrategyConfigDto, startDate should be before endDate");
        }
        double totalMoney = config.getStartMoney().doubleValue();

        // single strategy initialization
        StrategyConfig singleStrategy = null;
        Integer singlesPlanCount = 0;
        Integer singleTimeBound = 0;
        Map<Integer, List<TipSDto>> singlesPlansMap = null;
        Boolean[] couldAddToSinglePlanArray = null;
        int[] betWithoutWinArray = null;
        Integer BET_WITHOUT_WIN_MAXIMUM = null;
        Integer[] betArray = null;

        if (!Objects.isNull(config.getSingleStrategy()) && EStrategyPart.SINGLE.equals(config.getSingleStrategy().getStrategyPart())) {
            singleStrategy = config.getSingleStrategy();
            singleTimeBound = singleStrategy.getTimeBound();
            singlesPlanCount = singleStrategy.getTicketSize();
            singlesPlansMap = new HashMap<>(singlesPlanCount);
            couldAddToSinglePlanArray = new Boolean[singlesPlanCount];
            betWithoutWinArray = new int[singlesPlanCount];
            Arrays.fill(betWithoutWinArray, 0);
            BET_WITHOUT_WIN_MAXIMUM = singleStrategy.getBetWithoutWinMax();
            log.info("BET_WITHOUT_WIN_MAXIMUM: {}", BET_WITHOUT_WIN_MAXIMUM);
            betArray = new Integer[singlesPlanCount];
            Arrays.fill(betArray, singleStrategy.getBetAmount());
        }

        for (LocalDate day = config.getStartDate(); !day.isAfter(config.getEndDate()); day = day.plusDays(1L)) {
            List<Tip> tipsForEvaluation = tipService.getTipsFromPlanOnDay(day, config.getScoreMin());
            log.info("Evaluating {} tips for day {}; TotalMoney before the day: {}", tipsForEvaluation.size(), day, totalMoney);

            if (!Objects.isNull(singleStrategy) && EStrategyPart.SINGLE.equals(config.getSingleStrategy().getStrategyPart())) {
                Arrays.fill(couldAddToSinglePlanArray, true);
                for (int i = 0; i < singlesPlanCount; i++) {
                    singlesPlansMap.put(i, new ArrayList<>());
                }
                if (config.getSingleStrategy().getResetBetWithoutWinDailyFlag()) {
                    Arrays.fill(betWithoutWinArray, 0);
                    Arrays.fill(betArray, singleStrategy.getBetAmount());
                }
            }

            DayPlanRecord dayPlanRecord = new DayPlanRecord(totalMoney, singlesPlansMap);
            // single strategy initialization end

            for (Tip tip : tipsForEvaluation) {

         // single strategy preparation
                if (singleStrategy != null && hasAcceptableTipType(tip, singleStrategy) && hasAcceptableOdds(tip, singleStrategy)) {
                    // Добавляем тип в один из single tips планов
                    for (int i = 0; i < singlesPlanCount; i++) {
                        for (TipSDto tipDto : dayPlanRecord.singlePlanList(i)) {
                            if (tipDto.getPlayedOn().isAfter(tip.getGame().getPlayedOn().minusMinutes(singleTimeBound))
                                    &&  tipDto.getPlayedOn().isBefore(tip.getGame().getPlayedOn().plusMinutes(singleTimeBound))) {
                                couldAddToSinglePlanArray[i] = false;
                                break;
                            }
                        }
                    }
                    int[] singlePlansIndexesArray = Utils.randomUniqueNumbers(0, singlesPlanCount);
                    List<Integer> singlePlansIndexes = singleStrategy.getRandomSinglePlanInsertFlag()
                            ? Arrays.stream(singlePlansIndexesArray).boxed().collect(Collectors.toList())
                            : Arrays.stream(singlePlansIndexesArray).boxed().sorted().collect(Collectors.toList());

                    for (Integer singlePlanIndex : singlePlansIndexes) {
                        if (couldAddToSinglePlanArray[singlePlanIndex]) {
                            dayPlanRecord
                                    .singlePlanList(singlePlanIndex)
                                    .add(tipToPredictionOrikaMapper.map(tip, TipSDto.class));
                            break;
                        }
                    }

                  /*  if (shouldAddToSinglePlan) {
                        singleTips.add(tipToPredictionOrikaMapper.map(tip, ShortTipDto.class));
                    }*/
                }
    // combo strategy
               /* if (!Objects.isNull(config.getComboStrategy()) && !shouldAddToSinglePlan) {

                }*/
    // draw strategy
                if (!Objects.isNull(config.getDrawStrategy())) {

                }
    // p1x strategy
                if (!Objects.isNull(config.getP1XStrategy())) {

                }
    // system strategy
                if (!Objects.isNull(config.getSystemStrategy())) {

                }
            }

            // Расчет успеха

            // single strategy evaluation
            if (singleStrategy != null) {

                for (int i = 0; i < singlesPlanCount; i++) {
                    List<TipSDto> singlesPlan = dayPlanRecord
                            .getSinglesPlansMap()
                            .get(i);
                    for (TipSDto tipSDto : singlesPlan) {
                        totalMoney = totalMoney - betArray[i];
                        log.info("TotalMoney: {}, betWithoutWin[{}]: {}, bet: {}, status:{}", totalMoney, i, betWithoutWinArray[i], betArray[i],
                                tipSDto.getStatus());
                        if (totalMoney < 0) {
                            log.info("Total money less then 0!!! {}", totalMoney);
                        }
                        switch (tipSDto.getStatus()) {
                            case WON:
                                totalMoney = totalMoney + (betArray[i] * (singleStrategy.getConvertDnbToSpot() ? correctPickOdds(tipSDto.getOdds(), tipSDto.getPick()) : tipSDto.getOdds()));
                                betWithoutWinArray[i] = 0;
                                betArray[i] = singleStrategy.getBetAmount();
                                break;
                            case LOST:
                                betWithoutWinArray[i]++;
                                if (betWithoutWinArray[i] > BET_WITHOUT_WIN_MAXIMUM) {
                                    betWithoutWinArray[i] = 0;
                                    betArray[i] = singleStrategy.getBetAmount();
                                } else {
                                    betArray[i] = 2 * betArray[i];
                                }
                                break;
                            case DNB:
                                if (singleStrategy.getConvertDnbToSpot()) {
                                    betWithoutWinArray[i]++;
                                    if (betWithoutWinArray[i] > BET_WITHOUT_WIN_MAXIMUM) {
                                        betWithoutWinArray[i] = 0;
                                        betArray[i] = singleStrategy.getBetAmount();
                                    } else {
                                        betArray[i] = 2 * betArray[i];
                                    }
                                } else {
                                    totalMoney = totalMoney + betArray[i];
                                }
                                break;
                            default:
                                log.error("I didn't expect this pick {}", tipSDto.getStatus());
                                break;
                        }
                    }
                }
            }

            // todo for the rest strategies
        }



        // TODO go on
        return StrategyScoreDto.builder()
                .predictionStrategyConfig(config)
                .profit((long) (totalMoney - config.getStartMoney()))
                .evaluationDays(config.getEndDate().compareTo(config.getStartDate()))
                .build();
    }

    private Double correctPickOdds(Double odds, EPick pick) {
        switch (pick) {
            case SPOT_DNB_1:
            case SPOT_DNB_2:
                return odds * SPOT_DNB_COEF;
            case SPOT_1X:
            case SPOT_2X:
                return odds * SPOT_X1_COEF;
            default:
                return odds;
        }
    }

    private boolean hasAcceptableTipType(Tip tip, StrategyConfig strategy) {
        return strategy.getAcceptableTipTypes().contains(tip.getPick());
    }

    private boolean hasAcceptableOdds(Tip tip, StrategyConfig strategy) {
        return tip.getOdds() > strategy.getOddsMinMap().get(tip.getPick())
        && tip.getOdds() < strategy.getOddsMaxMap().get(tip.getPick());
    }
}
