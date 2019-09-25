package com.milan.tipster.service.impl;

import com.milan.tipster.error.exception.GameCanceledException;
import com.milan.tipster.model.Score;
import com.milan.tipster.model.enums.EScoreType;
import com.milan.tipster.service.ScoreService;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class ScoreServiceImpl implements ScoreService {

    @Override
    public Score makeScore(Element scoreEl) {

        Element timeLabelEl = scoreEl.selectFirst("span.time-label");
        if (timeLabelEl.hasText() && timeLabelEl.text().contains("AKYP")) {
            return Score.builder().scoreType(EScoreType.GAME_CANCELLED).build();
        }
        Elements eventSpanEls = timeLabelEl.select("span");
        if (eventSpanEls.size() == 2) {
            String scoreStr = eventSpanEls.get(0).text().replace(eventSpanEls.get(1).text(), "");
            String[] scoreParts = scoreStr.split("-");
            int homeGoals = Integer.parseInt(scoreParts[0].trim(), 10);
            int awayGoals = Integer.parseInt(scoreParts[1].trim(), 10);
            Score score = Score.builder()
                    .homeGoals(homeGoals)
                    .awayGoals(awayGoals)
                    .scoreType(determineScore(homeGoals, awayGoals))
                    .build();
            return score;
        }
        return null;
    }

    private EScoreType determineScore(int homeGoals, int awayGoals) {
        if (homeGoals < 0 || awayGoals < 0) {
            return EScoreType.UNKNOWN;
        }
        if (homeGoals > awayGoals) {
            return EScoreType.SPOT_1;
        } else if (homeGoals == awayGoals) {
            return EScoreType.SPOT_X;
        } else {
            return EScoreType.SPOT_2;
        }
    }
}
