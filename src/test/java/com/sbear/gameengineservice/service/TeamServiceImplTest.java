package com.sbear.gameengineservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;


import com.sbear.gameengineservice.dto.PlayerDTO;
import com.sbear.gameengineservice.dto.TeamDTO;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.Team;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import com.sbear.gameengineservice.repository.PlayerRepository;
import com.sbear.gameengineservice.repository.StatusOfMatchRepository;
import com.sbear.gameengineservice.repository.TeamRepository;
import com.sbear.gameengineservice.service.impl.TeamServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private StatusOfMatchRepository statusOfMatchRepository;

    @InjectMocks
    private TeamServiceImpl teamServiceImpl;

    @Mock
    private TeamService teamService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSetPlayersForTeamWhenTeamAndPlayersExist() {
        // Given
        Long teamId = 1L;
        Team team = new Team();
        Player player1 = new Player();
        player1.setId(1L);
        Player player2 = new Player();
        player2.setId(2L);
        List<PlayerDTO> playerDTOs = Arrays.asList(new PlayerDTO(1L), new PlayerDTO(2L));

        when(teamRepository.findById(Math.toIntExact(teamId))).thenReturn(Optional.of(team));
        when(playerRepository.findById(1L)).thenReturn(Optional.of(player1));
        when(playerRepository.findById(2L)).thenReturn(Optional.of(player2));

        // When
        teamServiceImpl.setPlayersForTeam(teamId, playerDTOs);

        // Then
        verify(teamRepository).findById(Math.toIntExact(teamId));
        verify(playerRepository).findById(1L);
        verify(playerRepository).findById(2L);
        verify(teamRepository).save(team);
        assertTrue(team.getPlayers().contains(player1));
        assertTrue(team.getPlayers().contains(player2));
    }

    @Test
    public void testSetPlayersForTeamWhenTeamDoesNotExist() {
        // Given
        long teamId = 1L;
        List<PlayerDTO> playerDTOs = Collections.singletonList(new PlayerDTO(1L));

        when(teamRepository.findById(Math.toIntExact(teamId))).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            teamServiceImpl.setPlayersForTeam(teamId, playerDTOs);
        });
    }

    @Test
    public void testSetPlayersForTeamWhenPlayerDoesNotExist() {
        // Given
        long teamId = 1L;
        Team team = new Team();
        List<PlayerDTO> playerDTOs = Collections.singletonList(new PlayerDTO(1L));

        when(teamRepository.findById(Math.toIntExact(teamId))).thenReturn(Optional.of(team));
        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            teamServiceImpl.setPlayersForTeam(teamId, playerDTOs);
        });
    }





    @Test
    public void testGetTeamSummaryWhenCoachDoesNotExist() {
        // Given
        Long coachId = 1L;
        when(teamRepository.findTeamSummaryByCoachId(coachId)).thenReturn(null);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            teamServiceImpl.getTeamSummary(coachId);
        });
    }

    @Test
    public void testConvertToTeamDto() {
        // Given
        Team team = new Team();
        team.setName("Team A");
        team.setTeamCaptain("Captain A");
        team.setCoachName("Coach A");
        team.setCountry("Country A");
        team.setOwner("Owner A");

        Player player = new Player();
        player.setId(1L);
        player.setName("Player A");
        player.setCountry("Country A");
        player.setRuns(1000L);
        player.setWickets(50L);
        player.setGender("Male");
        player.setDateOfBirth(LocalDate.of(1990, 5, 15));
        player.setHighScore(150);
        player.setPlayedMatches(100);

        team.setPlayers(Collections.singletonList(player));

        // When
        TeamDTO teamDTO = teamServiceImpl.convertToTeamDto(team);

        // Then
        assertEquals("Team A", teamDTO.getName());
        assertEquals("Captain A", teamDTO.getTeamCaptain());
        assertEquals("Coach A", teamDTO.getCoach());
        assertEquals("Country A", teamDTO.getCountry());
        assertEquals("Owner A", teamDTO.getOwner());

        PlayerDTO playerDTO = teamDTO.getPlayers().get(0);
        assertEquals(1L, playerDTO.getId());
        assertEquals("Player A", playerDTO.getName());
        assertEquals("Country A", playerDTO.getCountry());
        assertEquals(1000, playerDTO.getRuns());
        assertEquals(50, playerDTO.getWickets());
        assertEquals("Male", playerDTO.getGender());
        assertNotNull(playerDTO.getDateOfBirth());
        assertEquals(150, playerDTO.getHighScore());
        assertEquals(100, playerDTO.getPlayedMatches());
    }

    @Test
    public void testGetTheCountOfTheStagesStarted() {
        // Given
        String matchStageName = "SEMIFINAL";
        Long count = 5L;
        when(statusOfMatchRepository.findTopCountByNameOrderByCountDesc(matchStageName)).thenReturn(count);

        // When
        Long result = teamServiceImpl.getTheCountOfTheStagesStarted(matchStageName);

        // Then
        assertEquals(count, result);
    }



}
