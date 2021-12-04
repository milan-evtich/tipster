package com.milan.tipster.service.impl;

import com.milan.tipster.dao.TeamRepository;
import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Team;
import com.milan.tipster.service.CompetitionService;
import com.milan.tipster.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Override
    public Team findOrMakeTeam(String teamNameGr, String[] gameParts, int i, String gameCode, Competition competition) {
        int teamWordCount = teamNameGr.split(" ").length;
        String teamCode = null;
        try {
            if (gameParts != null) {
                StringBuilder teamCodeSB = new StringBuilder();
                int j = i;
                while (i < teamWordCount + j) {
                    teamCodeSB.append(gameParts[i]);
                    teamCodeSB.append("-");
                    i++;
                }
                teamCodeSB.deleteCharAt(teamCodeSB.lastIndexOf("-"));
                teamCode = teamCodeSB.toString();
            }
        } catch (Exception e){
            log.error("Error making teamCode for team {} and gameCode {}", teamNameGr, gameCode);
        }
        Team team;
        try {
            team = teamRepository.findByNameGr(teamNameGr);
        } catch (IncorrectResultSizeDataAccessException e) {
            // если есть несколько команд с таким именем
            team = findTeamInCompetitionByTeamNameGr(competition, teamNameGr);
        }
        if (team == null) {
            log.warn("----WARN----TEAM NOT FOUND----{}---gameCode:{}", teamNameGr, gameCode);
            team = Team.builder()
                    .code(teamCode)
                    .nameGr(teamNameGr)
                    .build();
            teamRepository.save(team);
            log.info("------NEW TEAM SAVED TO DB-------{}", team);
        } else if (StringUtils.isBlank(team.getCode()) && StringUtils.isNotBlank(teamCode)) {
            team.setCode(teamCode);
            team = teamRepository.save(team);
        }
        return team;
    }

    @Override
    public Team findByCode(String code) {
        return teamRepository.findByCode(code);
    }

    @Override
    public void addCodeToTeamWithNameGr(String code, String nameGr) {
        if (StringUtils.isNotBlank(nameGr)) {
            Team team = teamRepository.findByNameGr(nameGr);
            if (team != null && StringUtils.isNotBlank(code)) {
                team.setCode(code);
                teamRepository.save(team);
            }
        }
    }

    @Override
    public Team findTeamInCompetitionByTeamNameGr(Competition competition, String teamNameGr) {
        return competition.getTeams()
                .stream()
                .filter(t -> t.getNameGr().equalsIgnoreCase(teamNameGr))
                .findFirst()
                .orElse(null);
    }
}
