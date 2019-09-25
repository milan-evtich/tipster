package com.milan.tipster.service;

import com.milan.tipster.model.enums.ESeason;

import java.time.LocalDateTime;

public interface SeasonService {

    ESeason determineSeason(LocalDateTime gamePlayedOn);

    ESeason findSeasonByYear(String year);

}
