package com.milan.tipster.service.impl;

import com.milan.tipster.dao.CompetitionRepository;
import com.milan.tipster.dao.TipRepository;
import com.milan.tipster.dao.TipmanRepository;
import com.milan.tipster.error.exception.TipNotFoundException;
import com.milan.tipster.model.*;
import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.ETipStatus;
import com.milan.tipster.service.RatingService;
import com.milan.tipster.service.TipmanCompetitionRatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public int rateNewBaseOnTipsSinceDate(LocalDateTime startDateTime) {
        List<Tip> unratedNewTips = tipRepository.findAllByRatedFlag_NewRatedFalseAndGame_PlayedOnAfter(startDateTime);
        return rateTipsByNewCoefficient(unratedNewTips);
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
            if (!ratedFlag.isRated()) {
                if (!ratedFlag.isTcRated()) {
                    tcRatedNow = updateTCRating(tip.getTipId(), false);
                    ratedFlag.setTcRated(tcRatedNow);
                }
                if (!ratedFlag.isTipmanRated()) {
                    tipmanRatedNow = updateTipmanRating(tip.getTipId(), false);
                    ratedFlag.setTipmanRated(tipmanRatedNow);
                }
                if (!ratedFlag.isCompetitionRated()) {
                    competitionRatedNow = updateCompetitionRating(tip.getTipId(), false);
                    ratedFlag.setCompetitionRated(competitionRatedNow);
                }
            } else if (!ratedFlag.isNewRated()) {
                tcRatedNow = updateTCRating(tip.getTipId(), true);
                tipmanRatedNow = updateTipmanRating(tip.getTipId(), true);
                competitionRatedNow = updateCompetitionRating(tip.getTipId(), true);
            }

            if (tcRatedNow || tipmanRatedNow || competitionRatedNow) {
                count++;
            }
        }
        return count;
    }

    private int rateTipsByNewCoefficient(List<Tip> unratedTips) {
        int count = 0;
        for (Tip tip : unratedTips) {
            if (tip.getRatedFlag() == null) {
                tip.setRatedFlag(new RatedFlag());
            }
            RatedFlag ratedFlag = tip.getRatedFlag();
            if (!ratedFlag.isNewRated()) {
                boolean tcRatedNow = updateTCRating(tip.getTipId(), true);
                boolean tipmanRatedNow = updateTipmanRating(tip.getTipId(), true);
                boolean competitionRatedNow = updateCompetitionRating(tip.getTipId(), true);
                if (tcRatedNow || tipmanRatedNow || competitionRatedNow) {
                    count++;
                }
            }
        }
        return count;
    }


    // TODO check if new rating is working
    // set dnb and double chance for all tips

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
    private boolean updateTipmanRating(Long tipId, boolean onlyNew) {
        Tip tip = getTip(tipId);
        Objects.requireNonNull(tip.getTipman(), "Tipman is null!");

        Rating rating = tip.getTipman().getRating();

        boolean rated = false;
        if (!onlyNew) {
            rated = rate(tip, rating);
        }
        boolean ratedNew = rateNew(tip, rating);
        if (rated || ratedNew) {
            tipmanRepository.save(tip.getTipman());
        }
        return rated;
    }

    /**
     * Updates competition rating
     * @param tipId
     * @return
     */
    private boolean updateCompetitionRating(Long tipId, boolean onlyNew) {
        Tip tip = getTip(tipId);
        Objects.requireNonNull(tip.getGame(), "Game is null!");
        Objects.requireNonNull(tip.getGame().getCompetition(), "Competition is null!");

        Rating rating = tip.getGame().getCompetition().getRating();
        boolean rated = false;
        if (!onlyNew) {
            rated = rate(tip, rating);
        }
        boolean ratedNew = rateNew(tip, rating);
        if (rated || ratedNew) {
            competitionRepository.save(tip.getGame().getCompetition());
        }
        return rated;
    }

    /**
     * Updates complex (TipmanCompetitionRating) rating for tipman and competition
     * @param tipId
     * @return
     */
    private boolean updateTCRating(Long tipId, boolean onlyNew) {
        Tip tip = getTip(tipId);
        Objects.requireNonNull(tip.getTipman(), "Tipman is null!");
        Objects.requireNonNull(tip.getGame(), "Game is null!");
        Objects.requireNonNull(tip.getGame().getCompetition(), "Competition is null!");

        log.info("Rating tipman {}, tip {} and competition {}", tip.getTipman().getTipmanId(), tip.getTipId(), tip.getGame().getCompetition().getCompetitionId());
        TipmanCompetitionRating tcRating = findTipmanCompetitionRating(tip.getTipman(),
                tip.getGame().getCompetition());
        boolean rated  = false;
        if (!onlyNew) {
            rated = rate(tip, tcRating.getRating());
        }
        boolean ratedNew = rateNew(tip, tcRating.getRating());
        if (rated || ratedNew) {
            tipmanRepository.save(tip.getTipman());
        }
        return rated;


    }

    private boolean rate(Tip tip, Rating rating) {
        double odds = 1D;
        if (Objects.nonNull(tip.getOdds())) {
            odds = tip.getHotMatch() ? 2 * tip.getOdds() : tip.getOdds();
        }

        boolean result = updateRating(rating, tip.getStatus(), odds);
        updateNewRating(rating, tip.getStatus(), odds, tip.getPick());
        return result;
    }

    private boolean rateNew(Tip tip, Rating rating) {
        double odds = 1D;
        if (Objects.nonNull(tip.getOdds())) {
            odds = tip.getHotMatch() ? 2 * tip.getOdds() : tip.getOdds();
        }

        boolean result = updateNewRating(rating, tip.getStatus(), odds, tip.getPick());
        return result;
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

    private boolean updateNewRating(Rating rating, ETipStatus status, double odds, EPick pick) {
        switch (status) {
            case WON:
                rating.addMatch().addTip().addTipWon().addNewCoefficientWin(odds, pick);
                return true;
            case LOST:
                rating.addMatch().addTip().addTipLost().addNewCoefficientLost(odds, pick);
                return true;
            default:
                return false;
        }
    }

}
