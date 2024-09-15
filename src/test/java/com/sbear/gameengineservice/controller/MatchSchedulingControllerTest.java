package com.sbear.gameengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbear.gameengineservice.dto.MatchDetailsDTO;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(MatchSchedulingController.class)
public class MatchSchedulingControllerTest {

    @Mock
    private MatchSchedulingService matchSchedulingService;

    @Mock
    private TournamentService tournamentService;

    @InjectMocks
    private MatchSchedulingController matchSchedulingController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(matchSchedulingController).build();
        objectMapper = new ObjectMapper();
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
    public void testGetAllMatches_Success() throws Exception {
        List<MatchDetailsDTO> matches = Collections.singletonList(new MatchDetailsDTO());

        when(tournamentService.getAllMatches()).thenReturn(matches);

        mockMvc.perform(MockMvcRequestBuilders.get("/get-all-matches")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(matches)));
    }

    @Test
    public void testGetAllMatches_NoMatchesFound() throws Exception {
        when(tournamentService.getAllMatches()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/get-all-matches")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().json("[]"));
    }

    @Test
    public void testGetFinalScheduleMatches_Success() throws Exception {
        Long tournamentId = 1L;
        MatchDetailsDTO matchDetail = new MatchDetailsDTO();

        when(matchSchedulingService.getFinalScheduleMatches(tournamentId)).thenReturn(matchDetail);

        mockMvc.perform(MockMvcRequestBuilders.get("/final/schedule-matches/{tournamentId}", tournamentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(matchDetail)));
    }
}
