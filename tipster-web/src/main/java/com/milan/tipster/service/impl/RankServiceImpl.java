package com.milan.tipster.service.impl;

import com.milan.tipster.dao.CompetitionRepository;
import com.milan.tipster.dao.TipmanCompetitionRatingRepository;
import com.milan.tipster.dao.TipmanRepository;
import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Rankable;
import com.milan.tipster.model.Rateable;
import com.milan.tipster.model.Tipman;
import com.milan.tipster.model.TipmanCompetitionRating;
import com.milan.tipster.service.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.milan.tipster.model.Rateable.rateableActive;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankServiceImpl implements RankService {

    private final TipmanRepository tipmanRepository;
    private final CompetitionRepository competitionRepository;
    private final TipmanCompetitionRatingRepository tipmanCompetitionRatingRepository;

    @Override
    public Long getRankValue(Rankable rankable) {

        Integer maxCount = rankable.getMaxCount();
        Map<Long, Long> rankTableMap = rankable.getRankTableMap();
        Long rank = rankable.getRank();
        if (rank != null) {
            for (int i = 0; i < maxCount; i++) {
                if (rankTableMap.containsKey(rank)) {
                    return rankTableMap.get(rank);
                } else {
                    rank++;
                }
            }
        }
        return 1L; // default
    }

    @Override
    @Transactional
    public void rateAndRankTipmans(boolean rankByNewRating) {
        List<Tipman> all = tipmanRepository.findAll().stream()
                .filter(rateableActive())
                .collect(Collectors.toList());
        log.info("All active tipmans before rating: ");
        log.info(all.toString());

        // Rating
        rateAll(all);

        sortAll(all, rankByNewRating);

        // Ranking
        rankAll(all);

        log.info("All tipmans after ranking: ");
        log.info(all.toString());
    }

    @Override
    @Transactional
    public void rateAndRankCompetitions(boolean rankByNewRating) {
        List<Competition> all = competitionRepository.findAll()
                .stream()
                .filter(rateableActive())
                .collect(Collectors.toList());
        log.info("All competitions before rating: ");
        log.info(all.toString());

        // Rating
        rateAll(all);

        sortAll(all, rankByNewRating);

        // Ranking
        rankAll(all);

        log.info("All competitions after ranking: ");
        log.info(all.toString());
    }

    @Override
    @Transactional
    public void rateAndRankTipmanCompetitions(boolean rankByNewRating) {
        List<TipmanCompetitionRating> all = tipmanCompetitionRatingRepository.findAll()
                .stream()
                .filter(rateableActive())
                .collect(Collectors.toList());;
        log.info("All TipmanCompetitions before rating: ");
        log.info(all.toString());

        // Rating
        rateAll(all);

        sortAll(all, rankByNewRating);

        // Ranking
        rankAll(all);

        log.info("All TipmanCompetitions after ranking: ");
        log.info(all.toString());
    }

    private void rateAll(List<? extends Rateable> all) {
        all.forEach(Rateable::rateRateable);
        all.forEach(Rateable::rateNewRateable);
        log.info("All TipmanCompetitions after rating: ");
        log.info(all.toString());

    }

    private void sortAll(List<? extends Rateable> all, boolean rankByNewRating) {
        if (rankByNewRating) {
            sortAllByOverallRatingNewCoefficient(all);
        } else {
            sortAllByOverallScore(all);
        }
    }

    private void rankAll(List<? extends Rankable> all) {

        AtomicLong rank = new AtomicLong(1L);
        all.forEach(tipmanCompetitionRating -> tipmanCompetitionRating.setRank(rank.getAndIncrement()));
    }

    private void sortAllByOverallScore(List<? extends Rateable> all) {
        all.sort((o1, o2) -> Double.compare(
                o2.getRating().getOverallScore(),
                o1.getRating().getOverallScore())
        );
    }

    private void sortAllByOverallRatingNewCoefficient(List<? extends Rateable> all) {
        all.sort((o1, o2) -> Long.compare(
                o2.getRating().getOverallRating(),
                o1.getRating().getOverallRating())
        );
    }


}
