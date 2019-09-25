package com.milan.tipster.dao.impl;

import com.milan.tipster.dao.TipCustomRepository;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.ETipStatus;
import com.milan.tipster.model.enums.ETipType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Slf4j
@Component
public class TipCustomRepositoryImpl implements TipCustomRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public boolean updatePick(Long tipId, EPick pick) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateTipPick", Tip.class)
                    .setParameter(1, pick.name())
                    .setParameter(2, tipId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating tip pick {} with id {} ", pick, tipId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateOdds(Long tipId, Double odds) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateTipOdds", Tip.class)
                    .setParameter(1, odds)
                    .setParameter(2, tipId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating tip odds {} with id {} ", odds, tipId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateHotMatch(Long tipId, Boolean hotMatchFlag) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateTipHotMatch", Tip.class)
                    .setParameter(1, hotMatchFlag)
                    .setParameter(2, tipId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating tip hot match flag {} with id {} ", hotMatchFlag, tipId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateStatus(Long tipId, ETipStatus status) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateTipStatus", Tip.class)
                    .setParameter(1, status.name())
                    .setParameter(2, tipId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating tip status {} with id {} ", status , tipId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateType(Long tipId, ETipType type) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateTipType", Tip.class)
                    .setParameter(1, type.name())
                    .setParameter(2, tipId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating tip type {} with id {} ", type , tipId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateTipman(Long tipId, Long tipmanId) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateTipTipman", Tip.class)
                    .setParameter(1, tipmanId)
                    .setParameter(2, tipId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating tip tipman {} with id {} ", tipmanId , tipId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateGame(Long tipId, Long gameId) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateTipGame", Tip.class)
                    .setParameter(1, gameId)
                    .setParameter(2, tipId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating tip game {} with id {} ", gameId , tipId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateText(Long tipId, String text) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateTipText", Tip.class)
                    .setParameter(1, text)
                    .setParameter(2, tipId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating tip text {} with id {} ", text , tipId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateStrong(Long tipId, String strongText) {
        try {
            em.joinTransaction();
            em.createNamedQuery("updateTipStrong", Tip.class)
                    .setParameter(1, strongText)
                    .setParameter(2, tipId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating tip strong text {} with id {} ", strongText , tipId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateFetchStatus(Long tipId, EFetchStatus status) {
        try {
            em.createNamedQuery("updateTipFetchStatus", Tip.class)
                    .setParameter(1, status.name())
                    .setParameter(2, tipId)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            log.error("Error while updating tip fetch status {} with id {} ", status , tipId, e);
            return false;
        }
    }
}
