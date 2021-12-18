package com.milan.tipster.service;

import com.milan.tipster.model.Rankable;

public interface RankService {

    Long getRankValue(Rankable rankable);

    void rateAndRankTipmans(boolean rankByNewRating);

    void rateAndRankCompetitions(boolean rankByNewRating);

    void rateAndRankTipmanCompetitions(boolean rankByNewRating);
}
