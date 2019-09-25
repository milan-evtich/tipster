package com.milan.tipster.util;

import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Tipman;
import com.milan.tipster.service.CompetitionService;
import com.milan.tipster.service.RatingService;
import com.milan.tipster.service.TipmanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class RatingUpdaterCommandLineRunner implements CommandLineRunner {

    private final RatingService ratingService;
    private final TipmanService tipmanService;
    private final CompetitionService competitionService;

    @Override
    public void run(String... args) throws Exception {
        // UNCOMMENT IF NEED TO CREATE RATINGS
        // Update all tipmans - create tipman competition ratings
//        createAllTipmansTCRatings();

        // Update all ratings based on tips with ratedFlag=false and status NOT OPEN
        // TODO NEXT
        //  updateRatingsForTips();
    }

    private void createAllTipmansTCRatings() {

        List<Tipman> tipmans = tipmanService.findAll();
        if (tipmans != null) {
            for (Tipman tipman : tipmans) {
                createRatingForCompetitionAndTipman(tipman);
            }
        }
    }

    private void createRatingForCompetitionAndTipman(Tipman tipman) {

        List<Competition> tipmanCompetitions = competitionService.findAllTipmansCompetitions(tipman);
        if (tipmanCompetitions != null) {
            for (Competition competition : tipmanCompetitions) {
                ratingService.createTipmanCompetitionRatingIfNotExist(tipman, competition);
            }
        }
    }
}
