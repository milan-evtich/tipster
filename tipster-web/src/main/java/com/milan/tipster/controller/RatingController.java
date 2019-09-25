package com.milan.tipster.controller;

import com.milan.tipster.model.RatedFlag;
import com.milan.tipster.model.Tip;
import com.milan.tipster.service.RatingService;
import com.milan.tipster.service.TipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private TipService tipService;

    /**
     * Update all rating (tc, tip and competition) for tips where ratedFlag=false
     *
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/rating/update-unrated-tips")
    public String updateRatingsForUnratedTips() throws IOException {
        String resultFormat = "%s number of tips has been ratedFlag!";
        int count = 0;
        List<Tip> unratedTips = tipService.findAllUnratedTips();
        for (Tip tip : unratedTips) {
            boolean tcRatedNow = false;
            boolean tipmanRatedNow = false;
            boolean competitionRatedNow = false;
            if (tip.getRatedFlag() == null) {
                tip.setRatedFlag(new RatedFlag());
            }
            if (tip.getRatedFlag().getTcRated() == null) {
                tcRatedNow = ratingService.updateTCRating(tip.getTipId());
                tip.getRatedFlag().setTcRated(tcRatedNow);
            }
            if (tip.getRatedFlag().getTipmanRated() == null) {
                tipmanRatedNow = ratingService.updateTipmanRating(tip.getTipId());
                tip.getRatedFlag().setTipmanRated(tipmanRatedNow);
            }
            if (tip.getRatedFlag().getCompetitionRated() == null) {
                competitionRatedNow = ratingService.updateCompetitionRating(tip.getTipId());
                tip.getRatedFlag().setCompetitionRated(competitionRatedNow);
            }

            if (tcRatedNow || tipmanRatedNow || competitionRatedNow) {
                count++;
            }
            // TODO REMOVE AFTER FIRST SUCCESS
            break;
        }
        return String.format(resultFormat, count);
    }

}
