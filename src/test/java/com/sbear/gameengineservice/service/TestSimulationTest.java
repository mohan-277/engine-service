package com.sbear.gameengineservice.service;

import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.Team;
import com.sbear.gameengineservice.entity.stats.StatusOfMatch;
import com.sbear.gameengineservice.repository.PlayerRepository;
import com.sbear.gameengineservice.repository.StatusOfMatchRepository;
import com.sbear.gameengineservice.repository.TeamRepository;
import com.sbear.gameengineservice.utilities.PlayerUtil;
import com.sbear.gameengineservice.websocket.services.CricketMatchUtil;
import com.sbear.gameengineservice.websocket.services.CricketSimulation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TestSimulationTest {
    @Autowired
    private CricketSimulation cricketSimulation;

    @MockBean
    private TeamRepository teamRepository;

    @MockBean
    private PlayerRepository playerRepository;



    @MockBean
    private StatusOfMatchRepository statusOfMatchRepository;

    @Test
    public void testConvertDTOToCricketMatchUtil() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        MatchDetailsDTO matchDTO = new MatchDetailsDTO();
        matchDTO.setMatchId(1L);
        matchDTO.setMatchDateTime(now); // Set LocalDateTime directly
        matchDTO.setTeamA("Team A");
        matchDTO.setTeamB("Team B");

        Team teamA = new Team();
        teamA.setName("Team A");
        teamA.setId(1L);

        Team teamB = new Team();
        teamB.setName("Team B");
        teamB.setId(2L);

        Player playerA = new Player();
        playerA.setName("Player A");
        playerA.setDateOfBirth(LocalDate.of(1990, 1, 1));
        playerA.setSpecialization("Batsman");
        playerA.setGender("Male");
        playerA.setCountry("Country A");

        Player playerB = new Player();
        playerB.setName("Player B");
        playerB.setDateOfBirth(LocalDate.of(1991, 2, 2));
        playerB.setSpecialization("Bowler");
        playerB.setGender("Female");
        playerB.setCountry("Country B");

        when(teamRepository.findTeamByName("Team A")).thenReturn(teamA);
        when(teamRepository.findTeamByName("Team B")).thenReturn(teamB);
        when(playerRepository.findByTeamId(1L)).thenReturn(Arrays.asList(playerA));
        when(playerRepository.findByTeamId(2L)).thenReturn(Arrays.asList(playerB));

        // When
        CricketMatchUtil result = cricketSimulation.convertDTOToCricketMatchUtil(matchDTO);

        // Then
        assertNotNull(result);
        assertEquals("Team A", result.getTeamA().getName());
        assertEquals("Team B", result.getTeamB().getName());
        assertEquals(Long.valueOf(1), result.getMatchId());

        // Assert LocalDateTime
        LocalDateTime resultDateTime = result.getMatchDateTime();
        assertNotNull(resultDateTime);
        assertEquals(now.getYear(), resultDateTime.getYear());
        assertEquals(now.getMonth(), resultDateTime.getMonth());
        assertEquals(now.getDayOfMonth(), resultDateTime.getDayOfMonth());
        assertEquals(now.getHour(), resultDateTime.getHour());
        assertEquals(now.getMinute(), resultDateTime.getMinute());
        assertEquals(now.getSecond(), resultDateTime.getSecond());
        assertEquals(now.getNano(), resultDateTime.getNano());

        // Verify the team and player details
        List<PlayerUtil> teamAPlayers = result.getTeamA().getPlayers();
        assertNotNull(teamAPlayers);
        assertEquals(1, teamAPlayers.size());
        PlayerUtil playerUtilA = teamAPlayers.get(0);
        assertEquals("Player A", playerUtilA.getName());
        assertEquals(LocalDate.of(1990, 1, 1).toString(), playerUtilA.getDateOfBirth());
        assertEquals("Batsman", playerUtilA.getSpecialization());
        assertEquals("Male", playerUtilA.getGender());
        assertEquals("Country A", playerUtilA.getCountry());

        List<PlayerUtil> teamBPlayers = result.getTeamB().getPlayers();
        assertNotNull(teamBPlayers);
        assertEquals(1, teamBPlayers.size());
        PlayerUtil playerUtilB = teamBPlayers.get(0);
        assertEquals("Player B", playerUtilB.getName());
        assertEquals(LocalDate.of(1991, 2, 2).toString(), playerUtilB.getDateOfBirth());
        assertEquals("Bowler", playerUtilB.getSpecialization());
        assertEquals("Female", playerUtilB.getGender());
        assertEquals("Country B", playerUtilB.getCountry());
    }



    @Test
    public void testGetNextCountForMatchStage() {
        // Given
        String matchStageName = "Stage";
        StatusOfMatch latestStatus = new StatusOfMatch();
        latestStatus.setCount(5L);

        when(statusOfMatchRepository.findTopByMatchStageNameOrderByCountDesc(matchStageName))
                .thenReturn(latestStatus);

        // When
        Long result = cricketSimulation.getNextCountForMatchStage(matchStageName);

        // Then
        assertEquals(6L, result);
    }

    @Test
    public void testGetNextCountForMatchStageWhenNoStatus() {
        // Given
        String matchStageName = "Stage";

        when(statusOfMatchRepository.findTopByMatchStageNameOrderByCountDesc(matchStageName))
                .thenReturn(null);

        // When
        Long result = cricketSimulation.getNextCountForMatchStage(matchStageName);

        // Then
        assertEquals(1L, result);
    }
}
