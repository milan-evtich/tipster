package com.milan.tipster.service.impl;

import com.milan.tipster.dao.CompetitionRepository;
import com.milan.tipster.dao.TipRepository;
import com.milan.tipster.dao.TipmanRepository;
import com.milan.tipster.error.exception.TipNotFoundException;
import com.milan.tipster.error.exception.TipmanCompetitionRatingNotFoundException;
import com.milan.tipster.model.*;
import com.milan.tipster.model.enums.ETipStatus;
import com.milan.tipster.service.RatingService;
import com.milan.tipster.service.TipmanCompetitionRatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class RatingServiceImpl implements RatingService {

    @Autowired
    private TipmanRepository tipmanRepository;
    @Autowired
    private TipmanCompetitionRatingService tipmanCompetitionRatingService;
    @Autowired
    private TipRepository tipRepository;
    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public void createTipmanCompetitionRatingIfNotExist(Tipman tipman, Competition competition) {
        Objects.requireNonNull(tipman, "Tipman!");
        Objects.requireNonNull(competition, "Competition!");
        Long tipmanId = tipman.getTipmanId();
        Long competitionId = competition.getCompetitionId();
        Objects.requireNonNull(tipmanId, "Tipman Id!");
        Objects.requireNonNull(competitionId, "Competition Id!");

        TipmanCompetitionRating tipmanCompetitionRating = tipmanCompetitionRatingService
                .findTipmanCompetitionRating(tipmanId, competitionId);

        if (tipmanCompetitionRating == null) {
            log.info("CREATING NEW TipmanCompetitionRating for tipmanId: {} and competitionId: {}", tipmanId, competitionId);
            tipmanCompetitionRatingService.createNewTipmanCompetitionRating(tipmanId, competitionId);
        }
    }

    @Override
    public boolean updateTipmanRating(Long tipId) {
        Tip tip = getTip(tipId);
        Objects.requireNonNull(tip.getTipman(), "Tipman is null!");

        Rating rating = tip.getTipman().getRating();
        if (rating == null) {
            tip.getTipman().setRating(new Rating());

        }
        boolean rated = updateRating(rating, tip.getStatus());
        if (rated) {
            tipmanRepository.save(tip.getTipman());
        }
        return rated;
    }

    @Override
    public boolean updateCompetitionRating(Long tipId) {
        Tip tip = getTip(tipId);
        Objects.requireNonNull(tip.getGame(), "Game is null!");
        Objects.requireNonNull(tip.getGame().getCompetition(), "Competition is null!");

        Rating rating = tip.getGame().getCompetition().getRating();
        if (rating == null) {
            tip.getGame().getCompetition().setRating(new Rating());
        }
        boolean rated = updateRating(rating, tip.getStatus());
        if (rated) {
            competitionRepository.save(tip.getGame().getCompetition());
        }
        return rated;
    }

    @Override
    public boolean updateTCRating(Long tipId) {
        Tip tip = getTip(tipId);
        Objects.requireNonNull(tip.getTipman(), "Tipman is null!");
        Objects.requireNonNull(tip.getGame(), "Game is null!");
        Objects.requireNonNull(tip.getGame().getCompetition(), "Competition is null!");

        TipmanCompetitionRating tcRating = findTipmanCompetitionRating(tip.getTipman(),
                tip.getGame().getCompetition().getCode());
        Rating rating = tcRating.getRating();
        if (rating == null) {
            tcRating.setRating(new Rating());
        }
        boolean rated = updateRating(rating, tip.getStatus());
        if (rated) {
            // TODO check if works, if not use tipmanCompetitionRatingRepository
            tipmanRepository.save(tip.getTipman());
        }
        return rated;


    }

    private Tip getTip(Long tipId) {
        Objects.requireNonNull(tipId, "Tip id is null!");
        Tip tip = tipRepository.findById(tipId).get();
        if (tip == null) {
            throw new TipNotFoundException("Tip id:" + tipId);
        }
        return tip;
    }

    private TipmanCompetitionRating findTipmanCompetitionRating(Tipman tipman, String competitionCode) {
        for (TipmanCompetitionRating tcRating : tipman.getTipmanCompetitionRatings()) {
            if (tcRating.getCompetition().getCode().equalsIgnoreCase(competitionCode)) {
                return tcRating;
            } else {
                throw new TipmanCompetitionRatingNotFoundException("Tipman "
                        + tipman.getFullName() + ", competition " + competitionCode);
            }
        }
        return null;
    }

    private boolean updateRating(Rating rating, ETipStatus status) {
        switch (status) {
            case WON:
                rating.addMatch().addTip().addTipWon();
                return true;
            case LOST:
                rating.addMatch().addTip().addTipLost();
                return true;
            case DNB:
                rating.addMatch().addTip().addTipDNB();
                return true;
            case NOBET:
                rating.addMatch().addNobet();
                return true;
            case UNKNOWN:
                rating.addMatch().addUnknown();
                return true;
            case OPEN:
                return false;
        }
        return false;
    }

}
