package com.milan.tipster.dao.impl;

import com.milan.tipster.dao.TipmanCompetitionRatingRepository;
import com.milan.tipster.model.TipmanCompetitionRating;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class TipmanCompetitionRatingRepositoryImpl implements TipmanCompetitionRatingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public void createNewTipmanCompetitionRating(Long tipmanId, Long competitionId) {
        Objects.requireNonNull(tipmanId, "Tipman id!");
        Objects.requireNonNull(competitionId, "Competition id!");
        entityManager.createNativeQuery("insert into tipman_competition_rating (" +
                        "tipman_tipman_id," +
                        "competition_competition_id," +
                        "overall_match_count," +
                        "overall_rating," +
                        "overall_score," +
                        "overall_tip_count," +
                        "tips_lost," +
                        "tips_won," +
                        "nobet_count," +
                        "unknown_count," +
                        "overall_coefficient," +
                        "new_coefficient," +
                        "tipsdnb" +
                        ") values (?,?,?,?,?,?,?,?,?,?,?,?,?)")
                .setParameter(1, tipmanId)
                .setParameter(2, competitionId)
                .setParameter(3, 0L)
                .setParameter(4, 0L)
                .setParameter(5, 0D)
                .setParameter(6, 0L)
                .setParameter(7, 0L)
                .setParameter(8, 0L)
                .setParameter(9, 0L)
                .setParameter(10, 0L)
                .setParameter(11, 0D)
                .setParameter(12, 0D)
                .setParameter(13, 0L)
                .executeUpdate();
    }

    @Override
    public TipmanCompetitionRating findTipmanCompetitionRating(Long tipmanId, Long competitionId) {
        Objects.requireNonNull(tipmanId, "Tipman id!");
        Objects.requireNonNull(competitionId, "Competition id!");
        log.info("Getting timpanCompetitionRating for tipman {} and competition {}", tipmanId, competitionId);
        return entityManager.createNamedQuery("getTipmanCompetitionRatingByTipmanIdAndCompetitionId", TipmanCompetitionRating.class)
                .setParameter("tipmanId", tipmanId)
                .setParameter("competitionId", competitionId)
                .getSingleResult();
    }

    @Override
    public List<TipmanCompetitionRating> findAll() {
        return entityManager.createNamedQuery("getAllTipmanCompetitionRating", TipmanCompetitionRating.class)
                .getResultList();
    }
}
