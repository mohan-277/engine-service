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
    public void testGetMatchesByTypeAndGroup() {
        Long tournamentId = 1L;
        CricketMatch match1 = new CricketMatch();
        match1.setMatchGroup("Group A");
        CricketMatch match2 = new CricketMatch();
        match2.setMatchGroup("Group B");

        when(cricketMatchRepository.findMatchesByTournamentId(tournamentId))
                .thenReturn(Arrays.asList(match1, match2));

        Map<String, List<MatchDetailsDTO>> result = matchSchedulingService.getMatchesByTypeAndGroup(tournamentId);

        assertNotNull(result);
        assertEquals(1, result.get("Group A").size());
        assertEquals(1, result.get("Group B").size());
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
    public void testScheduleSemiFinals_GroupStageNotCompleted() {
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setTournamentName("Test Tournament");

        // Mocking repository responses
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(cricketMatchRepository.existsByTournamentIdAndMatchStage(tournamentId, "Knock out Stages")).thenReturn(false);

        // Simulate group stage matches not completed
        long groupStageMatchesCount = 1; // Simulate some group stage matches still planned
        when(cricketMatchRepository.countByTournamentIdAndMatchStageAndMatchStatus(tournamentId, MatchConstants.PLAY_OFF, MatchConstants.PLANNED))
                .thenReturn(groupStageMatchesCount);

        // Test should throw RuntimeException
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> matchSchedulingService.scheduleSemiFinals(tournamentId));
        assertEquals("Group stage matches are not yet completed.", thrown.getMessage());
    }

    @Test
    public void testScheduleSemiFinals_EmptyStats() {
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setTournamentName("Test Tournament");

        // Mocking repository responses
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(cricketMatchRepository.existsByTournamentIdAndMatchStage(tournamentId, "Knock out Stages")).thenReturn(false);

        // Simulate group stage matches completed
        long groupStageMatchesCount = 0; // Assume all group stage matches are completed
        when(cricketMatchRepository.countByTournamentIdAndMatchStageAndMatchStatus(tournamentId, MatchConstants.PLAY_OFF, MatchConstants.PLANNED))
                .thenReturn(groupStageMatchesCount);

        // Simulate empty stats for groups
        when(teamStatsRepository.findTeamStatsByMatchGroup("Group A")).thenReturn(Collections.emptyList());
        when(teamStatsRepository.findTeamStatsByMatchGroup("Group B")).thenReturn(Collections.emptyList());

        // Test should throw RuntimeException
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> matchSchedulingService.scheduleSemiFinals(tournamentId));
        assertEquals("Not enough teams to schedule semi-finals.", thrown.getMessage());
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

}

