package com.sbear.gameengineservice.service;


import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.*;
import com.sbear.gameengineservice.entity.constants.MatchConstants;
import com.sbear.gameengineservice.entity.stats.TeamStats;
import com.sbear.gameengineservice.repository.*;
import com.sbear.gameengineservice.repository.stats.TeamStatsRepository;
import com.sbear.gameengineservice.service.impl.MatchSchedulingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MatchSchedulingServiceTest {

    @Mock
    private CricketMatchRepository cricketMatchRepository;

    @Mock
    private TeamRegistrationRepository teamRegistrationRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private TeamStatsRepository teamStatsRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private MatchSchedulingService matchSchedulingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testScheduleGroupStageMatches() {
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setStartDate(LocalDateTime.now());
        tournament.setMatchInterval(Duration.ofDays(1));

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(cricketMatchRepository.existsByTournamentIdAndMatchStage(tournamentId, "PLAYOFF")).thenReturn(false);

        List<TeamRegistration> groupARegistrations = new ArrayList<>();
        List<TeamRegistration> groupBRegistrations = new ArrayList<>();
        when(teamRegistrationRepository.findTeamRegistrationByTournamentIdAndGroupType(tournamentId, "Group A"))
                .thenReturn(groupARegistrations);
        when(teamRegistrationRepository.findTeamRegistrationByTournamentIdAndGroupType(tournamentId, "Group B"))
                .thenReturn(groupBRegistrations);

        List<Location> allLocations = new ArrayList<>();
        when(locationRepository.findAll()).thenReturn(allLocations);

        assertThrows(RuntimeException.class, () -> matchSchedulingService.scheduleGroupStageMatches(tournamentId));

        // Add more test scenarios as needed
    }



    @Test
    public void testGetFinalScheduleMatches_NoTeams() {
        Long tournamentId = 1L;
        // No teams available for the final
        List<TeamStats> topTeams = Collections.emptyList();
        when(teamStatsRepository.findTop2TeamsByMatchGroupAndPoints("Knock out Stages")).thenReturn(topTeams);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            matchSchedulingService.getFinalScheduleMatches(tournamentId);
        });

        assertEquals("Not enough teams to schedule the final match. Knock-out stages might not be completed.", thrown.getMessage());
    }

    @Test
    public void testGetFinalScheduleMatches_Success() {
        Long tournamentId = 1L;
        Tournament mockTournament = new Tournament();
        mockTournament.setTournamentName("Test Tournament");

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(mockTournament));
        when(teamStatsRepository.findTop2TeamsByMatchGroupAndPoints("Knock out Stages"))
                .thenReturn(Arrays.asList(new TeamStats("Team A", 20), new TeamStats("Team B", 15)));
        when(locationRepository.findAll()).thenReturn(Collections.singletonList(new Location()));
        when(teamRepository.findTeamByName("Team A")).thenReturn(new Team());
        when(teamRepository.findTeamByName("Team B")).thenReturn(new Team());

        MatchDetailsDTO matchDetail = matchSchedulingService.getFinalScheduleMatches(tournamentId);

        assertNotNull(matchDetail);
    }


}

