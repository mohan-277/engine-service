package com.mohan.gameengineservice.controller;

import com.mohan.gameengineservice.dto.*;
import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Team;
import com.mohan.gameengineservice.entity.Tournament;
import com.mohan.gameengineservice.exceptions.ResourceNotFoundException;
import com.mohan.gameengineservice.service.TournamentService;
import com.mohan.gameengineservice.service.impl.MatchSchedulingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.mohan.gameengineservice.entity.Location;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TournamentController.class)
public class TournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TournamentService tournamentService;

    @MockBean
    private MatchSchedulingService matchSchedulingService;

    private TournamentDTO tournamentDTO;
    private TeamRegistrationDTO teamRegistrationDTO;

    @BeforeEach
    void setUp() {
        tournamentDTO = new TournamentDTO();
        tournamentDTO.setName("IPL 2024");
        tournamentDTO.setLocation("Mumbai");

        teamRegistrationDTO = new TeamRegistrationDTO();
        teamRegistrationDTO.setName("Chennai Super Kings");
    }

//    @Test
//    void testCreateTournament_Success() throws Exception {
//        Tournament savedTournament = new Tournament();
//        savedTournament.setTournamentName("IPL 2024");
//        savedTournament.setLocation("Mumbai");
//
//        Mockito.when(tournamentService.createTournament(any(TournamentDTO.class))).thenReturn(savedTournament);
//
//        mockMvc.perform(post("/api/admin/tournaments/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"name\": \"IPL 2024\", \"location\": \"Mumbai\" }"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.name").value("IPL 2024"))
//                .andExpect(jsonPath("$.location").value("Mumbai"));
//    }
    @Test
    void testCreateTournament_BadRequest() throws Exception {
        Mockito.when(tournamentService.createTournament(any(TournamentDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid tournament data"));

        mockMvc.perform(post("/api/admin/tournaments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"IPL 2024\", \"location\": \"Mumbai\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid tournament data"));
    }

    @Test
    void testGetAllTournaments() throws Exception {
        List<TournamentDTO> tournamentList = Arrays.asList(tournamentDTO);

        Mockito.when(tournamentService.getAllTournaments()).thenReturn(tournamentList);

        mockMvc.perform(get("/api/admin/tournaments/get-all-tournaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("IPL 2024"))
                .andExpect(jsonPath("$[0].location").value("Mumbai"));
    }

    @Test
    void testGetTournamentById_Success() throws Exception {
        Mockito.when(tournamentService.getTournamentById(1L)).thenReturn(tournamentDTO);

        mockMvc.perform(get("/api/admin/tournaments/tournament/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("IPL 2024"))
                .andExpect(jsonPath("$.location").value("Mumbai"));
    }

    @Test
    void testRegisterTeamByTournamentID_Success() throws Exception {
        Mockito.when(tournamentService.registerTeamByTournamentID(eq(1L), any(TeamRegistrationDTO.class)))
                .thenReturn("Team registered successfully");

        mockMvc.perform(post("/api/admin/tournaments/1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"teamName\": \"Chennai Super Kings\" }"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Team registered successfully"));
    }

//    @Test
//    void testGetRegisteredTeams() throws Exception {
//        TeamSummaryDTO teamSummary = new TeamSummaryDTO(1,"Chennai Super Kings", "MS Dhoni", "India","AKANKSHA","coach1");
//        List<TeamSummaryDTO> teamList = Arrays.asList(teamSummary);
//
//        Mockito.when(tournamentService.getRegisteredTeams(1L)).thenReturn(teamList);
//
//        mockMvc.perform(get("/api/admin/tournaments/1/teams"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].teamName").value("Chennai Super Kings"))
//                .andExpect(jsonPath("$[0].captainName").value("MS Dhoni"));
//    }

    @Test
    void testScheduleRoundRobin() throws Exception {
        MatchDetailsDTO matchDetailsDTO = new MatchDetailsDTO();
        matchDetailsDTO.setTeamA("Team A");
        matchDetailsDTO.setTeamB("Team B");

        List<MatchDetailsDTO> matchList = Arrays.asList(matchDetailsDTO);
        Mockito.when(matchSchedulingService.scheduleGroupStageMatches(1L)).thenReturn(matchList);

        mockMvc.perform(post("/api/admin/tournaments/1/schedule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamA").value("Team A"))
                .andExpect(jsonPath("$[0].teamB").value("Team B"));
    }

//    @Test
//    void testGetMatchesByTournamentId() throws Exception {
//        CricketMatch match = new CricketMatch();
//        match.setId(1L);
//        Team teamA = new Team();
//        teamA.setName("Team A");
//        Team teamB = new Team();
//        teamB.setName("Team B");
//        match.setTeamA(teamA);
//        match.setTeamB(teamB);
//
//        Mockito.when(matchSchedulingService.getMatchesByTypeAndGroup(1L))
//                .thenReturn(Map.of("Group Stage", Arrays.asList(match)));
//
//        mockMvc.perform(get("/api/admin/tournaments/1/matches"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.['Group Stage'][0].teamA").value("Team A"))
//                .andExpect(jsonPath("$.['Group Stage'][0].teamB").value("Team B"));
//    }
//
//    @Test
//    void testGetMatchById_Success() throws Exception {
//        CricketMatch match = new CricketMatch();
//        match.setId(1L);
//
//        Team teamA = new Team();
//        teamA.setName("Team A");
//        Team teamB = new Team();
//        teamB.setName("Team B");
//        match.setTeamA(teamA);
//        match.setTeamB(teamB);
//
//        Mockito.when(tournamentService.getCricketMatchById(1L)).thenReturn(match);
//
//        mockMvc.perform(get("/api/admin/tournaments/matches/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.teamA").value("Team A"))
//                .andExpect(jsonPath("$.teamB").value("Team B"));
//    }
//    @Test
//    void testUpdateMatch_Success() throws Exception {
//        // Mocking the service to return a success message
//        Mockito.when(tournamentService.updateCricketMatch(eq(1L), any(LocalDateTime.class), any(Location.class)))
//                .thenReturn("Match updated successfully");
//
//        mockMvc.perform(put("/api/admin/tournaments/matches/1")
//                        // Ensure correct formatting for LocalDateTime parameter
//                        .param("newDateTime", "2024-09-10T10:30:00")
//                        .param("newLocation", "New Stadium")) // Assuming Location is a String
//                .andExpect(status().isOk()) // Expecting status code 200
//                .andExpect(content().string("Match updated successfully")); // Expecting the right message
//    }

}
