package com.sbear.gameengineservice.service;

import com.sbear.gameengineservice.dto.PlayerDTO;
import com.sbear.gameengineservice.dto.TeamDTO;
import com.sbear.gameengineservice.dto.TeamSummary;

import java.util.List;

public interface TeamService {

    void setPlayersForTeam(Integer teamId, List<PlayerDTO> playerDTOs);

    TeamSummary getTeamSummary(Long coachId);

    TeamDTO getTeamById(Integer teamId);

    Long getTheCountOfTheStagesStarted(String matchStageName);

    List<TeamDTO> getAllTeams();

     List<TeamSummary> getAllTeamSummaries();

    void createTeam(TeamDTO teamDTO);
}
