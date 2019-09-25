package com.milan.tipster.service;

import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Team;

public interface TeamService {

    /**
     * ищет команду в БД по nameGr,
     * если не находит создает такую команду
     * @param teamNameGr
     * @param gameParts
     * @param i
     * @return
     */
    Team findOrMakeTeam(String teamNameGr, String[] gameParts, int i, String gameCode, Competition competition);

    Team findTeamInCompetitionByTeamNameGr(Competition competition, String teamNameGr);

    Team findByCode(String code);

    void addCodeToTeamWithNameGr(String code, String nameGr);

}
