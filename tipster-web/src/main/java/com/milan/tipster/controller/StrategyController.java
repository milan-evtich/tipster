package com.milan.tipster.controller;

import com.milan.tipster.dto.PredictionStrategyConfigDto;
import com.milan.tipster.dto.PredictionTipDto;
import com.milan.tipster.dto.StrategyScoreDto;
import com.milan.tipster.service.StrategyEvaluationService;
import com.milan.tipster.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/*
Контролирует разработку стратегии
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class StrategyController {

    private final StrategyEvaluationService strategyEvaluationService;

    @PostMapping("/strategy/emulate")
    ResponseEntity<StrategyScoreDto> getTipsPredictionForPeriod(@RequestBody @Valid PredictionStrategyConfigDto predictionStrategyConfigDto) {
        log.info("Start running emulation of strategy {}", predictionStrategyConfigDto);

        StrategyScoreDto strategyScoreDto = strategyEvaluationService.evaluate(predictionStrategyConfigDto);

        return ResponseEntity.ok(strategyScoreDto);
    }
}
