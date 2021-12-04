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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
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
    @Transactional
    public int rateBaseOnTipsSinceDate(LocalDateTime startDateTime) {
        List<Tip> unratedTips = tipRepository.findAllByStatusNotAndRatedFlag_RatedFalseAndGame_PlayedOnAfter(ETipStatus.OPEN, startDateTime);
        return rateTips(unratedTips);
    }

    @Override
    @Transactional
    public int rateTop100UnratedTips() {
        List<Tip> unratedTips = tipRepository.findTop100ByRatedFlag_RatedFalseAndStatusNot(ETipStatus.OPEN);
        return rateTips(unratedTips);
    }


    private int rateTips(List<Tip> unratedTips) {
        int count = 0;
        for (Tip tip : unratedTips) {
            boolean tcRatedNow = false;
            boolean tipmanRatedNow = false;
            boolean competitionRatedNow = false;
            if (tip.getRatedFlag() == null) {
                tip.setRatedFlag(new RatedFlag());
            }
            RatedFlag ratedFlag = tip.getRatedFlag();
            if (!ratedFlag.isTcRated()) {
                tcRatedNow = updateTCRating(tip.getTipId());
                ratedFlag.setTcRated(tcRatedNow);
            }
            if (!ratedFlag.isTipmanRated()) {
                tipmanRatedNow = updateTipmanRating(tip.getTipId());
                ratedFlag.setTipmanRated(tipmanRatedNow);
            }
            if (!ratedFlag.isCompetitionRated()) {
                competitionRatedNow = updateCompetitionRating(tip.getTipId());
                ratedFlag.setCompetitionRated(competitionRatedNow);
            }

            if (tcRatedNow || tipmanRatedNow || competitionRatedNow) {
                count++;
            }
        }
        return count;
    }

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

    /**
     * Updates tipman rating
     * @param tipId
     * @return
     */
    private boolean updateTipmanRating(Long tipId) {
        Tip tip = getTip(tipId);
        Objects.requireNonNull(tip.getTipman(), "Tipman is null!");

        Rating rating = tip.getTipman().getRating();

        boolean rated = rate(tip, rating);
        if (rated) {
            tipmanRepository.save(tip.getTipman());
        }
        return rated;
    }

    /**
     * Updates competition rating
     * @param tipId
     * @return
     */
    private boolean updateCompetitionRating(Long tipId) {
        Tip tip = getTip(tipId);
        Objects.requireNonNull(tip.getGame(), "Game is null!");
        Objects.requireNonNull(tip.getGame().getCompetition(), "Competition is null!");

        Rating rating = tip.getGame().getCompetition().getRating();
        boolean rated = rate(tip, rating);
        if (rated) {
            competitionRepository.save(tip.getGame().getCompetition());
        }
        return rated;
    }

    /**
     * Updates complex (TipmanCompetitionRating) rating for tipman and competition
     * @param tipId
     * @return
     */
    private boolean updateTCRating(Long tipId) {
        Tip tip = getTip(tipId);
        Objects.requireNonNull(tip.getTipman(), "Tipman is null!");
        Objects.requireNonNull(tip.getGame(), "Game is null!");
        Objects.requireNonNull(tip.getGame().getCompetition(), "Competition is null!");

        log.info("Rating tipman {}, tip {} and competition {}", tip.getTipman().getTipmanId(), tip.getTipId(), tip.getGame().getCompetition().getCompetitionId());
        TipmanCompetitionRating tcRating = findTipmanCompetitionRating(tip.getTipman(),
                tip.getGame().getCompetition());
        boolean rated = rate(tip, tcRating.getRating());
        if (rated) {
            tipmanRepository.save(tip.getTipman());
        }
        return rated;


    }

    private boolean rate(Tip tip, Rating rating) {
        double odds = 1D;
        if (Objects.nonNull(tip.getOdds())) {
            odds = tip.getHotMatch() ? 2 * tip.getOdds() : tip.getOdds();
        }
        return updateRating(rating, tip.getStatus(), odds);
    }

    private Tip getTip(Long tipId) {
        Objects.requireNonNull(tipId, "Tip id is null!");
        Tip tip = tipRepository.findById(tipId).get();
        if (tip == null) {
            throw new TipNotFoundException("Tip id:" + tipId);
        }
        return tip;
    }


    private TipmanCompetitionRating findTipmanCompetitionRating(Tipman tipman, Competition competition) {
        for (TipmanCompetitionRating tcRating : tipman.getTipmanCompetitionRatings()) {
            if (tcRating.getCompetition().getCode().equalsIgnoreCase(competition.getCode())) {
                return tcRating;
            }
        }
        tipmanCompetitionRatingService.createNewTipmanCompetitionRating(tipman.getTipmanId(), competition.getCompetitionId());
        return tipmanCompetitionRatingService.findTipmanCompetitionRating(tipman.getTipmanId(), competition.getCompetitionId());
    }

    private boolean updateRating(Rating rating, ETipStatus status, double odds) {
        switch (status) {
            case WON:
                rating.addMatch().addTip().addTipWon().addCoefficientWin(odds);
                return true;
            case LOST:
                rating.addMatch().addTip().addTipLost().addCoefficientLost(odds);
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
