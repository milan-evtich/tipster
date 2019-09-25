package com.milan.tipster.service;

import com.milan.tipster.dao.TipmanCompetitionRatingRepository;
import com.milan.tipster.dao.TipmanRepository;
import com.milan.tipster.model.TipmanCompetitionRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TipmanCompetitionRatingServiceImpl implements TipmanCompetitionRatingService {

    @Autowired
    private TipmanRepository tipmanRepository;

    @Autowired
    private TipmanCompetitionRatingRepository tipmanCompetitionRatingRepository;

    @Override
    public TipmanCompetitionRating findTipmanCompetitionRating(Long tipmanId, Long competitionId) {
        return tipmanRepository.findTipmanCompetitionRating(tipmanId, competitionId);
    }

    @Override
    public void createNewTipmanCompetitionRating(Long tipmanId, Long competitionId) {
        tipmanCompetitionRatingRepository.createNewTipmanCompetitionRating(tipmanId, competitionId);
    }

}
