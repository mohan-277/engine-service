package com.mohan.gameengineservice.service;

import com.mohan.gameengineservice.dto.MatchDetailsDTO;
import com.mohan.gameengineservice.dto.TeamRegistrationDTO;
import com.mohan.gameengineservice.dto.TeamSummaryDTO;
import com.mohan.gameengineservice.dto.TournamentDTO;
import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Tournament;

import java.util.List;

public interface TournamentService {

    Tournament createTournament(TournamentDTO tournamentDTO); // this is validated one the tournament size must be 6

    List<TournamentDTO> getAllTournaments();
    String registerTeamByTournamentID(Long tournamentId, TeamRegistrationDTO teamRegistrationDTO) throws Exception ;

    List<TeamSummaryDTO> getRegisteredTeams(Long tournamentId);

    TournamentDTO getTournamentById(Long tournamentId);

  List<MatchDetailsDTO> scheduleRoundRobinMatches(Long tournamentId);

    MatchDetailsDTO getCricketMatchById(Long matchId);
}
