package com.milan.tipster.dao.impl;

import com.milan.tipster.dao.TipmanCompetitionRatingRepository;
import com.milan.tipster.model.TipmanCompetitionRating;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Objects;

@Repository
public class TipmanCompetitionRatingRepositoryImpl implements TipmanCompetitionRatingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public void createNewTipmanCompetitionRating(Long tipmanId, Long competitionId) {
        Objects.requireNonNull(tipmanId, "Tipman id!");
        Objects.requireNonNull(competitionId, "Competition id!");
        entityManager.createNativeQuery("insert into tipman_competition_rating (tipman_tipman_id," +
                "competition_competition_id," +
                "overall_match_count," +
                "overall_rating," +
                "overall_score," +
                "overall_tip_count," +
                "tips_lost," +
                "tips_won," +
                "tipsdnb) values (?,?,?,?,?,?,?,?,?)")
                .setParameter(1, tipmanId)
                .setParameter(2, competitionId)
                .setParameter(3, 0L)
                .setParameter(4, 0L)
                .setParameter(5, 0D)
                .setParameter(6, 0L)
                .setParameter(7, 0L)
                .setParameter(8, 0L)
                .setParameter(9, 0L)
                .executeUpdate();
    }

    @Override
    public TipmanCompetitionRating findTipmanCompetitionRating(Long tipmanId, Long competitionId) {
        Objects.requireNonNull(tipmanId, "Tipman id!");
        Objects.requireNonNull(competitionId, "Competition id!");
        return entityManager.createNamedQuery("getTipmanCompetitionRatingByTipmanIdAndCompetitionId", TipmanCompetitionRating.class)
                .setParameter(1, tipmanId)
                .setParameter(2, competitionId)
                .getSingleResult();
    }
}
