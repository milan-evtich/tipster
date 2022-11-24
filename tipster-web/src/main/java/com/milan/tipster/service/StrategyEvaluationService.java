package com.milan.tipster.service;

import com.milan.tipster.dto.PredictionStrategyConfigDto;
import com.milan.tipster.dto.StrategyScoreDto;

public interface StrategyEvaluationService {

    StrategyScoreDto evaluate(PredictionStrategyConfigDto config);
    
}
