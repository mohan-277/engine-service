package com.sbear.gameengineservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbear.gameengineservice.controller.PlayerController;
import com.sbear.gameengineservice.dto.*;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

public class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(playerController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testRegisterPlayer_Success() throws Exception {
        PlayerDTO playerDTO = new PlayerDTO();
        Player savedPlayer = new Player();

        when(playerService.registerPlayer(playerDTO)).thenReturn(savedPlayer);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/engine-service/player/player/create-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedPlayer)));
    }

    @Test
    public void testRegisterPlayer_InvalidDateFormat() throws Exception {
        PlayerDTO playerDTO = new PlayerDTO();

        when(playerService.registerPlayer(playerDTO)).thenThrow(new DateTimeParseException("Invalid date", "date", 0));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/engine-service/player/player/create-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid date format: Invalid date"));
    }

    @Test
    public void testGetPlayer_Success() throws Exception {
        Long playerId = 1L;
        PlayerDTO playerDTO = new PlayerDTO();

        when(playerService.getPlayerById(playerId)).thenReturn(playerDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/engine-service/player/player/profile/{id}", playerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(playerDTO)));
    }

    @Test
    public void testGetPlayer_NotFound() throws Exception {
        Long playerId = 1L;

        when(playerService.getPlayerById(playerId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/engine-service/player/player/profile/{id}", playerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Player not found"));
    }

    @Test
    public void testGetAllPlayersByCountry_Success() throws Exception {
        String country = "India";
        List<PlayerDTO> playerDTOs = Collections.singletonList(new PlayerDTO());

        when(playerService.getAllPlayersByCountry(country)).thenReturn(playerDTOs);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/engine-service/player/list-players/{country}", country)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(playerDTOs)));
    }

    @Test
    public void testGetAllPlayers_Success() throws Exception {
        List<PlayerDTO> playerDTOs = Collections.singletonList(new PlayerDTO());

        when(playerService.getAllPlayers()).thenReturn(playerDTOs);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/engine-service/player/list-players")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(playerDTOs)));
    }

    @Test
    public void testGetAllPlayersCountry_Success() throws Exception {
        List<String> countries = Collections.singletonList("India");

        when(playerService.getAllPlayersCountry()).thenReturn(countries);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/engine-service/player/list-players/country")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(countries)));
    }

    @Test
    public void testGetPlayerScoreCard_Success() throws Exception {
        String playerName = "Sachin";
        PlayerScoreCardDTO scoreCardDTO = new PlayerScoreCardDTO();

        when(playerService.getPlayerScoreCard(playerName)).thenReturn(scoreCardDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/engine-service/player/player-score-card/{playerName}", playerName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(scoreCardDTO)));
    }
}
