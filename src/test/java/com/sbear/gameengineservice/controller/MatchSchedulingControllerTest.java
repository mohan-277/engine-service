package com.sbear.gameengineservice.controller;


import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.service.TournamentService;
import com.sbear.gameengineservice.service.impl.MatchSchedulingService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchSchedulingController.class)
public class MatchSchedulingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchSchedulingService matchSchedulingService;

    @MockBean
    private TournamentService tournamentService;

    @Test
    public void testGetSemiFinalScheduleMatches_Success() throws Exception {
        Long tournamentId = 1L;
        List<MatchDetailsDTO> mockMatchDetails = Arrays.asList(new MatchDetailsDTO(), new MatchDetailsDTO());

        when(matchSchedulingService.scheduleSemiFinals(tournamentId)).thenReturn(mockMatchDetails);

        mockMvc.perform(get("/semifinal/schedule-matches/{tournamentId}", tournamentId))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect((ResultMatcher) jsonPath("$", hasSize(mockMatchDetails.size())));
    }

    @Test
    public void testGetSemiFinalScheduleMatches_Failure() throws Exception {
        Long tournamentId = 1L;
        when(matchSchedulingService.scheduleSemiFinals(tournamentId)).thenThrow(new RuntimeException("Error scheduling semifinals"));

        mockMvc.perform(get("/semifinal/schedule-matches/{tournamentId}", tournamentId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllMatches_Success() throws Exception {
        // Create mock MatchDetailsDTO instances
        List<MatchDetailsDTO> mockMatches = Arrays.asList(
                new MatchDetailsDTO("1", "Team A vs Team B", "2024-09-16T10:00:00Z"),
                new MatchDetailsDTO("2", "Team C vs Team D", "2024-09-17T15:00:00Z")
        );

        // Mock the behavior of the service
        when(tournamentService.getAllMatches()).thenReturn(mockMatches);

        // Perform the GET request and validate the response
        mockMvc.perform(get("/api/get-all-matches"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect((ResultMatcher) jsonPath("$", hasSize(mockMatches.size())))
                .andExpect((ResultMatcher) jsonPath("$[0].matchId", is("1")))
                .andExpect((ResultMatcher) jsonPath("$[1].matchId", is("2")));
    }


    @Test
    public void testGetAllMatches_NotFound() throws Exception {
        when(tournamentService.getAllMatches()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/get-all-matches"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllMatches_Exception() throws Exception {
        // Mock the exception thrown by the service
        when(tournamentService.getAllMatches()).thenThrow(new RuntimeException("Error retrieving matches"));

        // Perform the GET request and verify the response status is 500
        mockMvc.perform(get("/api/get-all-matches")) // Ensure the URL is correct
                .andExpect(status().isNotFound());
    }
}
