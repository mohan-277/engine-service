package com.sbear.gameengineservice.service;

import com.sbear.gameengineservice.dto.*;
import com.sbear.gameengineservice.entity.Tournament;
import com.sbear.gameengineservice.entity.stats.PlayerStats;
import com.sbear.gameengineservice.entity.stats.TeamStats;

import java.util.List;

public interface TournamentService {

    Tournament createTournament(TournamentDTO tournamentDTO); // this is validated one the tournament size must be 6

    List<TournamentDTO> getAllTournaments();
    String registerTeamByTournamentID(Long tournamentId, TeamRegistrationDTO teamRegistrationDTO) throws Exception ;

    List<TeamSummaryDTO> getRegisteredTeams(Long tournamentId);

    TournamentDTO getTournamentById(Long tournamentId);

  List<MatchDetailsDTO> scheduleRoundRobinMatches(Long tournamentId);

    MatchDetailsDTO getCricketMatchById(Long matchId);

    List<PlayerStats> getALlPlayerStats(Long matchId);

    List<TeamStats> getAllTeamStats(Long matchId);

    List<TeamStats> getAllTeamStatsByMatchGroup(String matchGroupType);

    List<MatchDetailsDTO> getAllMatches();

    MatchResponseDTO getMatchDetails(Long matchId);
}
