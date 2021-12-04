package com.milan.tipster.controller;

import com.milan.tipster.service.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RankingController {

    private final RankService rankService;

    @GetMapping(value = "/ranking/tipmans")
    public String updateTipmanRanking() {
        rankService.rateAndRankTipmans();
        return "Tipmans ranked";
    }

    @GetMapping(value = "/ranking/competitions")
    public String updateCompetitionRanking() {
        rankService.rateAndRankCompetitions();
        return "Competitions ranked";
    }

    @GetMapping(value = "/ranking/tipman-competitions")
    public String updateTipmanCompetitionRanking() {
        rankService.rateAndRankTipmanCompetitions();
        return "TipmanCompetitions ranked";
    }
}
