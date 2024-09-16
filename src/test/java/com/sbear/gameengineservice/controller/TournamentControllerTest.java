package com.sbear.gameengineservice.controller;

import com.sbear.gameengineservice.dto.*;
import com.sbear.gameengineservice.entity.Tournament;
import com.sbear.gameengineservice.entity.stats.PlayerStats;
import com.sbear.gameengineservice.entity.stats.TeamStats;
import com.sbear.gameengineservice.service.TournamentService;
import com.sbear.gameengineservice.service.impl.MatchSchedulingService;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TournamentControllerTest {

    @InjectMocks
    private TournamentController tournamentController;

    @Mock
    private TournamentService tournamentService;

    @Mock
    private MatchSchedulingService matchSchedulingService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateTournament_Success() {
        TournamentDTO tournamentDTO = new TournamentDTO();
        Tournament savedTournament = new Tournament();
        when(tournamentService.createTournament(any(TournamentDTO.class))).thenReturn(savedTournament);

        ResponseEntity<?> response = tournamentController.createTournament(tournamentDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedTournament, response.getBody());
    }

    @Test
    public void testCreateTournament_Failure() {
        TournamentDTO tournamentDTO = new TournamentDTO();
        when(tournamentService.createTournament(any(TournamentDTO.class)))
                .thenThrow(new IllegalArgumentException("At least 6 teams are required"));

        ResponseEntity<?> response = tournamentController.createTournament(tournamentDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("At least 6 teams are required", response.getBody());
    }

    @Test
    public void testGetAllTournamentsCreatedAdmin() {
        List<TournamentDTO> tournaments = new ArrayList<>();
        when(tournamentService.getAllTournaments()).thenReturn(tournaments);

        ResponseEntity<List<TournamentDTO>> response = tournamentController.getAllTournamentsCreatedAdmin();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tournaments, response.getBody());
    }

    @Test
    public void testRegisterTeamByTournamentID_Success() throws Exception {
        TeamRegistrationDTO registrationDTO = new TeamRegistrationDTO();
        when(tournamentService.registerTeamByTournamentID(anyLong(), any(TeamRegistrationDTO.class)))
                .thenReturn("Team registered successfully in Group A");

        ResponseEntity<?> response = tournamentController.registerTeamByTournamentID(1L, registrationDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Team registered successfully in Group A", response.getBody());
    }

    @Test
    public void testRegisterTeamByTournamentID_Failure() throws Exception {
        TeamRegistrationDTO registrationDTO = new TeamRegistrationDTO();
        when(tournamentService.registerTeamByTournamentID(anyLong(), any(TeamRegistrationDTO.class)))
                .thenThrow(new IllegalArgumentException("Tournament is full"));

        ResponseEntity<?> response = tournamentController.registerTeamByTournamentID(1L, registrationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Tournament is full", response.getBody());
    }

    @Test
    public void testGetRegisteredTeams() {
        List<TeamSummaryDTO> teamSummaries = new ArrayList<>();
        when(tournamentService.getRegisteredTeams(anyLong())).thenReturn(teamSummaries);

        ResponseEntity<List<TeamSummaryDTO>> response = tournamentController.getRegisteredTeams(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(teamSummaries, response.getBody());
    }

    @Test
    public void testScheduleRoundRobin_Success() {
        List<MatchDetailsDTO> matchDetails = new ArrayList<>();
        when(matchSchedulingService.scheduleGroupStageMatches(anyLong())).thenReturn(matchDetails);

        ResponseEntity<?> response = tournamentController.scheduleRoundRobin(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(matchDetails, response.getBody());
    }

    @Test
    public void testGetMatchesByTournamentId() {
        Map<String, List<MatchDetailsDTO>> matches = new HashMap<>();
        when(matchSchedulingService.getMatchesByTypeAndGroup(anyLong())).thenReturn(matches);

        ResponseEntity<?> response = tournamentController.getMatchesByTournamentId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(matches, response.getBody());
    }

    @Test
    public void testGetTournamentById() {
        TournamentDTO tournamentDTO = new TournamentDTO();
        when(tournamentService.getTournamentById(anyLong())).thenReturn(tournamentDTO);

        ResponseEntity<TournamentDTO> response = tournamentController.getTournamentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tournamentDTO, response.getBody());
    }

    @Test
    public void testGetMatchById_Success() {
        MatchDetailsDTO matchDetails = new MatchDetailsDTO();
        when(tournamentService.getCricketMatchById(anyLong())).thenReturn(matchDetails);

        ResponseEntity<?> response = tournamentController.getMatchById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(matchDetails, response.getBody());
    }

    @Test
    public void testGetMatchById_NotFound() {
        when(tournamentService.getCricketMatchById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Match not found with ID: 1"));

        ResponseEntity<?> response = tournamentController.getMatchById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Match not found with ID: 1", response.getBody());
    }

    @Test
    public void testGetALlPlayerStatsByMatchId() {
        List<PlayerStats> playerStats = new ArrayList<>();
        when(tournamentService.getALlPlayerStats(anyLong())).thenReturn(playerStats);

        ResponseEntity<List<PlayerStats>> response = tournamentController.getALlPlayerStatsByMatchId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(playerStats, response.getBody());
    }

    @Test
    public void testGetAllTeamStatsByMatchId() {
        List<TeamStats> teamStats = new ArrayList<>();
        when(tournamentService.getAllTeamStats(anyLong())).thenReturn(teamStats);

        ResponseEntity<List<TeamStats>> response = tournamentController.getAllTeamStatsByMatchId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(teamStats, response.getBody());
    }


}
