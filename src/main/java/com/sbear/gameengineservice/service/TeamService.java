package com.sbear.gameengineservice.service;

import com.sbear.gameengineservice.dto.PlayerDTO;
import com.sbear.gameengineservice.dto.TeamDTO;
import com.sbear.gameengineservice.dto.TeamSummary;
import com.sbear.gameengineservice.entity.Team;

import java.util.List;

public interface TeamService {

    void setPlayersForTeam(Long teamId, List<PlayerDTO> playerDTOs);
    TeamSummary getTeamSummary(Long coachId);
    TeamDTO convertToTeamDto(Team team);
    Long getTheCountOfTheStagesStarted(String matchStageName);

}
