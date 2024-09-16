package com.sbear.gameengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbear.gameengineservice.controller.TeamController;
import com.sbear.gameengineservice.dto.PlayerDTO;
import com.sbear.gameengineservice.dto.TeamDTO;
import com.sbear.gameengineservice.dto.TeamSummary;
import com.sbear.gameengineservice.service.TeamService;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

public class TeamControllerTest {

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetAllTeams_Success() throws Exception {
        List<TeamDTO> teamDTOs = Collections.singletonList(new TeamDTO());

        when(teamService.getAllTeams()).thenReturn(teamDTOs);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/engine-service/teams/list-teams")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(teamDTOs)));
    }

    @Test
    public void testGetTeamSummary_NotFound() throws Exception {
        Long coachId = 1L;

        when(teamService.getTeamSummary(coachId)).thenThrow(new ResourceNotFoundException("coach id not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/engine-service/teams/list-teams-summary/{coachId}", coachId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("coach id not found"));
    }

    @Test
    public void testCreateTeam_Success() throws Exception {
        TeamDTO teamDTO = new TeamDTO();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/engine-service/teams/create-team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Team created successfully"));
    }

//    @Test
//    public void testCreateTeam_Error() throws Exception {
//        TeamDTO teamDTO = new TeamDTO();
//
//        when(teamService.createTeam(teamDTO)).thenThrow(new RuntimeException("Error creating team"));
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/engine-service/teams/create-team")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(teamDTO)))
//                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
//                .andExpect(MockMvcResultMatchers.content().string("Error creating team: Error creating team"));
//    }

    @Test
    public void testGetTeamByTeamId_Success() throws Exception {
        Integer teamId = 1;
        TeamDTO teamDTO = new TeamDTO();

        when(teamService.getTeamById(teamId)).thenReturn(teamDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/engine-service/teams/get/{teamId}", teamId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(teamDTO)));
    }

    @Test
    public void testGetTeamByTeamId_NotFound() throws Exception {
        Integer teamId = 1;

        // Configure the mock to throw an exception
        when(teamService.getTeamById(teamId)).thenThrow(new RuntimeException("Team not found"));

        // Perform the request and check status and content
        mockMvc.perform(MockMvcRequestBuilders.get("/api/engine-service/teams/get/{teamId}", teamId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Team not found"));
    }


    @Test
    public void testAddPlayersToTeam_Success() throws Exception {
        Integer teamId = 1;
        List<PlayerDTO> playerDTOs = Collections.singletonList(new PlayerDTO());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/engine-service/teams/{teamId}/players", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerDTOs)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Players added successfully"));
    }
}
