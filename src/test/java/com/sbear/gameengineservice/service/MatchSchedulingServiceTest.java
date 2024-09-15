package com.sbear.gameengineservice.service;

import com.sbear.gameengineservice.dto.MatchDetailsDTO;

import com.sbear.gameengineservice.entity.CricketMatch;
import com.sbear.gameengineservice.entity.Location;
import com.sbear.gameengineservice.entity.Team;
import com.sbear.gameengineservice.entity.constants.TournamentStatus;

import com.sbear.gameengineservice.repository.CricketMatchRepository;
import com.sbear.gameengineservice.repository.LocationRepository;
import com.sbear.gameengineservice.repository.TeamRegistrationRepository;
import com.sbear.gameengineservice.repository.TournamentRepository;
import com.sbear.gameengineservice.service.impl.MatchSchedulingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MatchSchedulingServiceTest {

    @InjectMocks
    private MatchSchedulingService matchSchedulingService;

    @Mock
    private CricketMatchRepository cricketMatchRepository;

    @Mock
    private TeamRegistrationRepository teamRegistrationRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMatchesByTypeAndGroup() {
        // Arrange
        Long tournamentId = 1L;
        List<CricketMatch> mockMatches = new ArrayList<>();

        // Add mock data to the list
        CricketMatch match = new CricketMatch();
        match.setId(1L);
        Team teamA = new Team();
        teamA.setName("Team A");
        match.setTeamA(teamA);
        Team teamB = new Team();
        teamB.setName("Team B");
        match.setTeamB(teamB);
        Location location = new Location();
        location.setCountry("Country");
        location.setGround("Ground");
        match.setLocation(location);
        match.setMatchType("T20");
        match.setMatchStage("Group Stage");
        match.setMatchGroup("Group A");
        match.setMatchDateTime(LocalDateTime.of(2024, 9, 15, 14, 0));

        mockMatches.add(match);
        when(cricketMatchRepository.findMatchesByTournamentId(tournamentId)).thenReturn(mockMatches);


    }


    @Test
    void testConvertToMatchDetailsDTOWithReflection() throws Exception {
        // Arrange
        MatchSchedulingService service = new MatchSchedulingService();
        CricketMatch match = new CricketMatch();
        match.setId(1L);

        // Initialize mock objects
        Team teamA = new Team();
        teamA.setName("Team A");
        match.setTeamA(teamA);

        Team teamB = new Team();
        teamB.setName("Team B");
        match.setTeamB(teamB);

        Location location = new Location();
        location.setCountry("Country");
        location.setGround("Ground");
        match.setLocation(location);

        match.setMatchType("T20");
        match.setMatchStage("Group Stage");
        match.setMatchGroup("Group A");
        match.setMatchDateTime(LocalDateTime.of(2024, 9, 15, 14, 0));

        // Access private method using reflection
        Method method = MatchSchedulingService.class.getDeclaredMethod("convertToMatchDetailsDTO", CricketMatch.class);
        method.setAccessible(true);

        // Act
        MatchDetailsDTO matchDetail = (MatchDetailsDTO) method.invoke(service, match);

        // Assert
        assertNotNull(matchDetail);
        assertEquals(1L, matchDetail.getMatchId());
        assertEquals("Team A", matchDetail.getTeamA());
        assertEquals("Team B", matchDetail.getTeamB());
        assertEquals(LocalDateTime.of(2024, 9, 15, 14, 0), matchDetail.getMatchDateTime());
        assertEquals("Country - Ground", matchDetail.getLocation());
        assertEquals("T20", matchDetail.getMatchType());
        assertEquals("Group Stage", matchDetail.getMatchStage());
        assertEquals(TournamentStatus.PLANNED.name(), matchDetail.getMatchStatus());
        assertEquals("Group A", matchDetail.getMatchGroup());
    }

}
