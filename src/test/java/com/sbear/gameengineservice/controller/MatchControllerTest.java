package com.sbear.gameengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.CricketMatch;
import com.sbear.gameengineservice.service.MatchService;
import com.sbear.gameengineservice.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchService matchService;

    @MockBean
    private TeamService teamService;

    @Test
    void testGetAllMatches() throws Exception {
        List<MatchDetailsDTO> matches = Arrays.asList(new MatchDetailsDTO(), new MatchDetailsDTO());
        when(matchService.getAllMatches()).thenReturn(matches);

        mockMvc.perform(get("/api/engine-service/match/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testAddMatch() throws Exception {
        CricketMatch match = new CricketMatch();
        // Set necessary fields in match

        when(matchService.addMatch(any(CricketMatch.class))).thenReturn(match);

        mockMvc.perform(post("/api/engine-service/match/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(match)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Match added successfully")));
    }

    @Test
    void testCreateMatch() throws Exception {
        MatchDetailsDTO dto = new MatchDetailsDTO();
        // Set necessary fields in dto

        when(matchService.createMatch(any(MatchDetailsDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/engine-service/match/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matchId").exists());
    }

    @Test
    void testGetMatch() throws Exception {
        Long matchId = 1L;

        // Create and initialize the DTO
        MatchDetailsDTO dto = new MatchDetailsDTO();
        dto.setMatchId(matchId);
        dto.setMatchType("Test Match");
        // Initialize other fields as necessary

        // Mock the service call
        when(matchService.matchDetailsGetByMatchId(matchId)).thenReturn(dto);

        // Perform the request and validate the response
        mockMvc.perform(get("/api/engine-service/match/get-match/{matchId}", matchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchId").value(matchId))
                .andExpect(jsonPath("$.matchType").value("Test Match"));
        // Add other field validations as needed
    }
    @Test
    void testGetTheCountOfTheStagesStarted() throws Exception {
        String stageName = "Group Stage";
        Long count = 5L;

        when(teamService.getTheCountOfTheStagesStarted(stageName)).thenReturn(count);

        mockMvc.perform(get("/api/engine-service/match/get-count/{matchStageName}", stageName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(count));
    }
}
