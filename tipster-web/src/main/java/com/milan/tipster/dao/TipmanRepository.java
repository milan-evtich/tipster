package com.milan.tipster.dao;

import com.milan.tipster.model.Tipman;
import com.milan.tipster.model.TipmanCompetitionRating;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TipmanRepository extends CrudRepository<Tipman, Long> {

    List<Tipman> findByFullName(String fullName);

    Tipman findOneByFullName(String fullName);

    @Query("select tcr from TipmanCompetitionRating tcr " +
            "where tcr.tipman.tipmanId = ?1 " +
            "and tcr.competition.competitionId = ?2")
    TipmanCompetitionRating findTipmanCompetitionRating(Long tipmanId, Long competitionId);

}
