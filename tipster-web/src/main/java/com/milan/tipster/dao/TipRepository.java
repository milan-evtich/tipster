package com.milan.tipster.dao;

import com.milan.tipster.model.Game;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.Tipman;
import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.ETipStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TipRepository extends CrudRepository<Tip, Long> {

    Tip findByGameAndTipman(Game game, Tipman tipman);

    List<Tip> findAllByStatus(ETipStatus status);

    List<Tip> findAllByStatusAndGame_PlayedOnAfter(ETipStatus status, LocalDateTime playedOn);

    List<Tip> findAllByStatusAndGame_PlayedOnBetween(ETipStatus status, LocalDateTime playedOnStart, LocalDateTime playedOnEnd);

    List<Tip> findDistinctByTipman_FullName(String fullName);

    List<Tip> findAllByRatedFlagIsNullOrRatedFlag_TipmanRatedOrRatedFlag_CompetitionRatedOrRatedFlag_TcRated(Boolean rated,
                                                                                                             Boolean competitionRated, Boolean tcRated);
    List<Tip> findAllByStatusNotAndRatedFlag_RatedFalseAndGame_PlayedOnAfter(ETipStatus status, LocalDateTime playedAfterDateTime);

    List<Tip> findAllByRatedFlag_NewRatedFalseAndGame_PlayedOnAfter(LocalDateTime playedAfterDateTime);

    List<Tip> findAllByPick(EPick unknown);

    List<Tip> findAllByFetchStatus(EFetchStatus fetchStatus);

    List<Tip> findAllByOdds(Double odds);

    List<Tip> findTop100ByRatedFlag_RatedFalseAndStatusNot(ETipStatus status);

}
