package com.milan.tipster.controller;

import com.milan.tipster.service.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RankingController {

    private final RankService rankService;

    @GetMapping(value = "/ranking/tipmans")
    public String updateTipmanRanking(@RequestParam(required = false) Boolean rankByNewRating) {
        rankService.rateAndRankTipmans(Objects.nonNull(rankByNewRating) ? rankByNewRating : false);
        return "Tipmans ranked";
    }

    @GetMapping(value = "/ranking/competitions")
    public String updateCompetitionRanking(@RequestParam(required = false) Boolean rankByNewRating) {
        rankService.rateAndRankCompetitions(Objects.nonNull(rankByNewRating) ? rankByNewRating : false);
        return "Competitions ranked";
    }

    @GetMapping(value = "/ranking/tipman-competitions")
    public String updateTipmanCompetitionRanking(@RequestParam(required = false) Boolean rankByNewRating) {
        rankService.rateAndRankTipmanCompetitions(Objects.nonNull(rankByNewRating) ? rankByNewRating : false);
        return "TipmanCompetitions ranked";
    }
}
