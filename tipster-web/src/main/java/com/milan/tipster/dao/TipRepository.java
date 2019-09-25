package com.milan.tipster.dao;

import com.milan.tipster.model.Game;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.Tipman;
import com.milan.tipster.model.enums.EFetchStatus;
import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.ETipStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TipRepository extends CrudRepository<Tip, Long> {

    Tip findByGameAndTipman(Game game, Tipman tipman);

    List<Tip> findAllByStatus(ETipStatus status);

    List<Tip> findDistinctByTipman_FullName(String fullName);

    List<Tip> findAllByRatedFlagIsNullOrRatedFlag_TipmanRatedOrRatedFlag_CompetitionRatedOrRatedFlag_TcRated(Boolean rated,
                                                                                                             Boolean competitionRated,
                                                                                                             Boolean tcRated);

    List<Tip> findAllByPick(EPick unknown);

    List<Tip> findAllByFetchStatus(EFetchStatus fetchStatus);

    List<Tip> findAllByOdds(Double odds);
}
