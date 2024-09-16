package com.sbear.gameengineservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sbear.gameengineservice.dto.PlayerDTO;
import com.sbear.gameengineservice.dto.PlayerScoreCardDTO;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.stats.PlayerStats;
import com.sbear.gameengineservice.exceptions.PlayerNotFoundException;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import com.sbear.gameengineservice.repository.PlayerRepository;
import com.sbear.gameengineservice.repository.stats.PlayerStatsRepository;
import com.sbear.gameengineservice.service.impl.PlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PlayerServiceImplTest {

    @Mock
    private PlayerStatsRepository playerStatsRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testGetPlayerScoreCard_NoStatsFound() {
        String playerName = "Jane Doe";
        when(playerStatsRepository.findByPlayerName(playerName)).thenReturn(Collections.emptyList());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> playerService.getPlayerScoreCard(playerName));
        assertEquals("No stats found for player: " + playerName, thrown.getMessage());
    }

    @Test
    public void testRegisterPlayer_Success() throws DateTimeParseException {
        PlayerDTO playerDTO = new PlayerDTO("Jane Doe", "USA", "2000-01-01", "Female", "All-Rounder");
        Player player = Player.builder()
                .name("Jane Doe")
                .country("USA")
                .dateOfBirth(LocalDate.parse("2000-01-01"))
                .gender("Female")
                .specialization("All-Rounder")
                .build();

        when(playerRepository.save(any(Player.class))).thenReturn(player);

        Player result = playerService.registerPlayer(playerDTO);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals("USA", result.getCountry());
        assertEquals(LocalDate.parse("2000-01-01"), result.getDateOfBirth());
        assertEquals("Female", result.getGender());
        assertEquals("All-Rounder", result.getSpecialization());
    }

    @Test
    public void testGetPlayerById_Success() throws PlayerNotFoundException {
        Long playerId = 1L;
        Player player = Player.builder()
                .id(playerId)
                .name("John Doe")
                .country("USA")
                .dateOfBirth(LocalDate.parse("1990-05-15"))
                .gender("Male")
                .specialization("Batsman")
                .build();

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));

        PlayerDTO result = playerService.getPlayerById(playerId);

        assertNotNull(result);
        assertEquals(playerId, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("USA", result.getCountry());
        assertEquals("1990-05-15", result.getDateOfBirth());
        assertEquals("Male", result.getGender());
        assertEquals("Batsman", result.getSpecialization());
    }

    @Test
    public void testGetPlayerById_NotFound() {
        Long playerId = 1L;
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        PlayerNotFoundException thrown = assertThrows(PlayerNotFoundException.class, () -> playerService.getPlayerById(playerId));
        assertEquals(Long.toString(playerId), thrown.getMessage());
    }

    @Test
    public void testGetAllPlayersByCountry_Success() {
        String country = "India";
        Player player = Player.builder()
                .name("Sachin Tendulkar")
                .country(country)
                .dateOfBirth(LocalDate.parse("1973-04-24"))
                .gender("Male")
                .specialization("Batsman")
                .build();

        when(playerRepository.findAllByCountry(country)).thenReturn(List.of(player));

        List<PlayerDTO> result = playerService.getAllPlayersByCountry(country);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Sachin Tendulkar", result.get(0).getName());
        assertEquals(country, result.get(0).getCountry());
    }

    @Test
    public void testGetAllPlayersByCountry_NotFound() {
        String country = "Nepal";
        when(playerRepository.findAllByCountry(country)).thenReturn(Collections.emptyList());

        ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> playerService.getAllPlayersByCountry(country));
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatusCode());
        assertEquals("No players found for country: " + country, thrown.getReason());
    }

    @Test
    public void testGetAllPlayers_Success() {
        Player player = Player.builder()
                .name("Virat Kohli")
                .country("India")
                .dateOfBirth(LocalDate.parse("1988-11-05"))
                .gender("Male")
                .specialization("Batsman")
                .build();

        when(playerRepository.findAll()).thenReturn(List.of(player));

        List<PlayerDTO> result = playerService.getAllPlayers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Virat Kohli", result.get(0).getName());
    }

    @Test
    public void testGetAllPlayers_NotFound() {
        when(playerRepository.findAll()).thenReturn(Collections.emptyList());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> playerService.getAllPlayers());
        assertEquals("No players found", thrown.getMessage());
    }

    @Test
    public void testGetAllPlayersCountry() {
        List<String> countries = List.of("India", "Australia", "England");
        when(playerRepository.findDistinctCountries()).thenReturn(countries);

        List<String> result = playerService.getAllPlayersCountry();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("India"));
        assertTrue(result.contains("Australia"));
        assertTrue(result.contains("England"));
    }
}

