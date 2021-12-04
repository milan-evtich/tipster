package com.milan.tipster.dao;

import com.milan.tipster.model.Tipman;
import com.milan.tipster.model.TipmanCompetitionRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TipmanRepository extends JpaRepository<Tipman, Long> {

    List<Tipman> findByFullName(String fullName);

    Tipman findOneByFullName(String fullName);

    @Query("select tcr from TipmanCompetitionRating tcr " +
            "where tcr.tipman.tipmanId = ?1 " +
            "and tcr.competition.competitionId = ?2")
    TipmanCompetitionRating findTipmanCompetitionRating(Long tipmanId, Long competitionId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "INSERT INTO  public.tipman_competition (competition_id, tipman_id) VALUES (:competitionId, :tipmanId)")
    void createNewTipmanCompetition(@Param("tipmanId") Long tipmanId,
                                    @Param("competitionId") Long competitionId);
}
