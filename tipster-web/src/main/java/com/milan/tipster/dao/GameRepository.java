package com.milan.tipster.dao;

import com.milan.tipster.model.Game;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.enums.EFetchStatus;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long> {

    List<Game> findAllByPlayedOnIsNull();

    List<Game> findAllByFetchStatus(EFetchStatus fetchStatus);

    Boolean existsByCode(String code);

    List<Game> findByPlayedOnBetween(LocalDateTime start, LocalDateTime end);


//    List<Game> findByPlayedOnAfterAndPlayedOnBefore(LocalDateTime start, LocalDateTime end);

    // Unused
    List<Game> findByHomeTeam_Name(String hostName);

    List<Game> findByAwayTeam_Name(String visitorName);

    List<Game> findAllByHomeTeam_NameOrAwayTeam_Name(String hostName, String visitorName);

    Game findByCode(String code);

}
