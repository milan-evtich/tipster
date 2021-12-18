package com.milan.tipster.controller;

import com.milan.tipster.service.RatingService;
import com.milan.tipster.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RestController
public class RatingController {

    @Autowired
    private RatingService ratingService;

    /**
     * Update rating (tc, tip and competition) for top 100 tips where rated=false
     *
     * @return
     */
    @GetMapping(value = "/rating/update-top-100-unrated-tips")
    public String rateBaseOnTipsSinceDate() {
        log.info("Start rating all unrated tips");

        int count = ratingService.rateTop100UnratedTips();

        String resultFormat = "%s number of tips has been ratedFlag!";
        return String.format(resultFormat, count);
    }

    /**
     * Update rating (tc, tip and competition) for tips where rated=false since date
     *
     * @return
     */
    @GetMapping(value = "/rating/update-unrated-tips/{startDateTimeStr}")
    public String rateBaseOnTipsSinceDate(@PathVariable String startDateTimeStr) {
        log.info("Start rating based on unrated tips since date and time {}", startDateTimeStr);

        LocalDateTime startDateTime = Utils.convertStringToLocalDateTime(startDateTimeStr, "dd-MM-yy-HH-mm");

        int count = ratingService.rateBaseOnTipsSinceDate(startDateTime);

        String resultFormat = "%s number of tips has been ratedFlag!";
        return String.format(resultFormat, count);
    }


    /**
     * Update rating (tc, tip and competition) for tips where ratedNew=false since date
     *
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/rating/update-unratedNew-tips/{startDateTimeStr}")
    public String rateNewBaseOnTipsSinceDate(@PathVariable String startDateTimeStr) {
        log.info("Start rating rateNew based on rateNew == false for tips since date and time {}", startDateTimeStr);

        LocalDateTime startDateTime = Utils.convertStringToLocalDateTime(startDateTimeStr, "dd-MM-yy-HH-mm");

        int count = ratingService.rateNewBaseOnTipsSinceDate(startDateTime);

        String resultFormat = "%s number of tips has been ratedNewFlag!";
        return String.format(resultFormat, count);
    }



}
