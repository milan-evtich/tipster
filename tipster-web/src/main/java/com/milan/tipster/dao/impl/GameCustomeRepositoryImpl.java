package com.milan.tipster.dao.impl;

import com.milan.tipster.dao.GameCustomeRepository;
import com.milan.tipster.model.Game;
import com.milan.tipster.model.Score;
import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.EPick;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class GameCustomeRepositoryImpl implements GameCustomeRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public boolean updatePlayedOn(Long gameId, LocalDateTime playedOn) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateGamePlayedOnDate", Game.class)
                    .setParameter(1, playedOn)
                    .setParameter(2, gameId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating game played on date {} with id {} ", playedOn, gameId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateNameGr(Long gameId, String gameNameGr) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateGameNameGr", Game.class)
                    .setParameter(1, gameNameGr)
                    .setParameter(2, gameId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating game nameGr {} with id {} ", gameNameGr, gameId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateFetchStatus(Long gameId, EFetchStatus fetchStatus) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateGameFetchStatus", Game.class)
                    .setParameter(1, fetchStatus.name())
                    .setParameter(2, gameId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating game fetch status {} with id {} ", fetchStatus, gameId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateScore(Long gameId, Score score) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateGameScore", Game.class)
                    .setParameter(1, score.getScoreType().name())
                    .setParameter(2, score.getHomeGoals())
                    .setParameter(3, score.getAwayGoals())
                    .setParameter(4, gameId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating game score {} with id {} ", score, gameId, e);
            return false;
        }
    }

    @Override
    public List<Game> findGamesByPick(EPick pick) {
        Objects.requireNonNull(pick);
        return em.createNamedQuery("getGamesByPick", Game.class)
                .setParameter(1, pick.name())
                .getResultList();
    }

}
