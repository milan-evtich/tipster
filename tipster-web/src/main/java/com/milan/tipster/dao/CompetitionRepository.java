package com.milan.tipster.dao;

import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Country;
import com.milan.tipster.model.enums.ESeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    Competition findByNameGrOrNameGr(String nameGr1, String nameGr2);

    Competition findBySeasonAndNameGrOrNameGr(ESeason season, String nameGr1, String nameGr2);

    Competition findByNameGr(String nameGr);

    Competition findByNameGrAndSeason(String nameGr, ESeason season);

    List<Competition> findAllByCountry(Country country);

    List<Competition> findAllByCountryAndSeason(Country country, ESeason season);

    @Query("SELECT DISTINCT c FROM Tip t, Game g, Competition c, Tipman tt " +
            "    WHERE t.game.gameId = g.gameId " +
            "    AND c.competitionId = g.competition.competitionId " +
            "    AND t.tipman.tipmanId = tt.tipmanId " +
            "    AND tt.fullName = ?1")
    List<Competition> tipmansCompetitions(String fullName);

}
