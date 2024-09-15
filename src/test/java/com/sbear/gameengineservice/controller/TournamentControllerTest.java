package com.sbear.gameengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbear.gameengineservice.dto.*;
import com.sbear.gameengineservice.entity.Tournament;
import com.sbear.gameengineservice.entity.stats.PlayerStats;
import com.sbear.gameengineservice.entity.stats.TeamStats;
import com.sbear.gameengineservice.service.TournamentService;
import com.sbear.gameengineservice.service.impl.MatchSchedulingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;


public class TournamentControllerTest {

    @Mock
    private TournamentService tournamentService;

    @Mock
    private MatchSchedulingService matchSchedulingService;

    @InjectMocks
    private TournamentController tournamentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tournamentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateTournament_Success() throws Exception {
        TournamentDTO tournamentDTO = new TournamentDTO();
        Tournament savedTournament = new Tournament();

        when(tournamentService.createTournament(tournamentDTO)).thenReturn(savedTournament);

        mockMvc.perform(MockMvcRequestBuilders.post("/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tournamentDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedTournament)));
    }

    @Test
    public void testGetAllTournaments_Success() throws Exception {
        List<TournamentDTO> tournaments = Collections.singletonList(new TournamentDTO());

        when(tournamentService.getAllTournaments()).thenReturn(tournaments);

        mockMvc.perform(MockMvcRequestBuilders.get("/get-all-tournaments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(tournaments)));
    }

    @Test
    public void testRegisterTeamByTournamentID_Success() throws Exception {
        Long tournamentId = 1L;
        TeamRegistrationDTO teamRegistrationDTO = new TeamRegistrationDTO();
        String result = "Team registered successfully";

        when(tournamentService.registerTeamByTournamentID(tournamentId, teamRegistrationDTO)).thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.post("/{tournamentId}/register", tournamentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRegistrationDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string(result));
    }

    @Test
    public void testGetRegisteredTeams_Success() throws Exception {
        Long tournamentId = 1L;
        List<TeamSummaryDTO> teams = Collections.singletonList(new TeamSummaryDTO());

        when(tournamentService.getRegisteredTeams(tournamentId)).thenReturn(teams);

        mockMvc.perform(MockMvcRequestBuilders.get("/{tournamentId}/teams", tournamentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(teams)));
    }

    @Test
    public void testGetSemiFinalScheduleMatches_Success() throws Exception {
        Long tournamentId = 1L;
        List<MatchDetailsDTO> matchDetails = Collections.singletonList(new MatchDetailsDTO());

        when(matchSchedulingService.scheduleSemiFinals(tournamentId)).thenReturn(matchDetails);

        mockMvc.perform(MockMvcRequestBuilders.get("/semifinal/schedule-matches/{tournamentId}", tournamentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(matchDetails)));
    }

    @Test
    public void testGetMatchById_Success() throws Exception {
        Long matchId = 1L;
        MatchDetailsDTO matchDetailsDTO = new MatchDetailsDTO();

        when(tournamentService.getCricketMatchById(matchId)).thenReturn(matchDetailsDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/matches/{matchId}", matchId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(matchDetailsDTO)));
    }
}