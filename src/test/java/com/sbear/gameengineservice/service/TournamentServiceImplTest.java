package com.sbear.gameengineservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sbear.gameengineservice.dto.*;
import com.sbear.gameengineservice.entity.*;
import com.sbear.gameengineservice.entity.constants.TournamentStatus;
import com.sbear.gameengineservice.repository.*;
import com.sbear.gameengineservice.repository.stats.PlayerStatsRepository;
import com.sbear.gameengineservice.repository.stats.TeamStatsRepository;
import com.sbear.gameengineservice.service.impl.TournamentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TournamentServiceImplTest {

    @Mock
    private TeamStatsRepository teamStatsRepository;

    @Mock
    private BallRepository ballRepository;

    @Mock
    private InningsRepository inningsRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private TeamRegistrationRepository teamRegistrationRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private CricketMatchRepository cricketMatchRepository;

    @Mock
    private PlayerStatsRepository playerStatsRepository;

    @InjectMocks
    private TournamentServiceImpl tournamentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRegisteredTeams_Success() {
        Long tournamentId = 1L;
        Team team = new Team();
        team.setTeamId(1L);
        team.setName("Team A");
        team.setCountry("Country A");
        team.setTeamCaptain("Captain A");
        team.setCoachName("Coach A");
        team.setOwner("Owner A");

        TeamRegistration registration = new TeamRegistration();
        registration.setTeam(team);

        List<TeamRegistration> registrations = Collections.singletonList(registration);
        when(teamRegistrationRepository.findTeamRegistrationByTournamentId(tournamentId)).thenReturn(registrations);

        List<TeamSummaryDTO> result = tournamentService.getRegisteredTeams(tournamentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        TeamSummaryDTO dto = result.get(0);
        assertEquals(1, dto.getId());
        assertEquals("Team A", dto.getName());
        assertEquals("Country A", dto.getCountry());
        assertEquals("Captain A", dto.getTeamCaptain());
        assertEquals("Coach A", dto.getCoach());
        assertEquals("Owner A", dto.getOwner());
    }

    @Test
    public void testCreateTournament_Success() {
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTournamentName("Tournament A");
        tournamentDTO.setTournamentType("Type A");
        tournamentDTO.setLocation("Location A");
        tournamentDTO.setStartDate(LocalDateTime.now().toString());
        tournamentDTO.setMatchInterval(Duration.ofDays(10));
        tournamentDTO.setNumberOfTeams(6);

        Tournament tournament = Tournament.builder()
                .tournamentName("Tournament A")
                .tournamentType("Type A")
                .location("Location A")
                .startDate(LocalDateTime.now())
                .matchInterval(Duration.ofDays(1))
                .numberOfTeams(6)
                .status(TournamentStatus.PLANNED)
                .build();

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        Tournament result = tournamentService.createTournament(tournamentDTO);

        assertNotNull(result);
        assertEquals("Tournament A", result.getTournamentName());
    }

    @Test
    public void testGetAllTournaments_Success() {
        Tournament tournament = new Tournament();
        tournament.setId(1L);
        tournament.setTournamentName("Tournament A");

        List<Tournament> tournaments = Collections.singletonList(tournament);
        when(tournamentRepository.findAll()).thenReturn(tournaments);

        List<TournamentDTO> result = tournamentService.getAllTournaments();

        assertNotNull(result);
        assertEquals(1, result.size());
        TournamentDTO dto = result.get(0);
        assertEquals(1L, dto.getTournamentDTOId());
        assertEquals("Tournament A", dto.getTournamentName());
    }

    @Test
    public void testRegisterTeamByTournamentID_Success() throws Exception {
        Long tournamentId = 1L;
        TeamRegistrationDTO teamRegistrationDTO = new TeamRegistrationDTO();
        teamRegistrationDTO.setTeamID(1);
        teamRegistrationDTO.setCoachName("Coach A");
        teamRegistrationDTO.setGroupType("Group A");

        Tournament tournament = new Tournament();
        tournament.setNumberOfTeams(6);
        tournament.setTeamRegistrations(new ArrayList<>());

        Team team = new Team();
        team.setTeamId(1L);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(teamRegistrationRepository.existsByTournamentIdAndTeamTeamId(tournamentId, 1)).thenReturn(false);
        when(teamRegistrationRepository.countByTournamentIdAndGroupType(tournamentId, "Group A")).thenReturn(0L);
        when(teamRegistrationRepository.countByTournamentIdAndGroupType(tournamentId, "Group B")).thenReturn(0L);

        String result = tournamentService.registerTeamByTournamentID(tournamentId, teamRegistrationDTO);

        assertEquals("Team registered successfully in Group A", result);
        verify(teamRegistrationRepository).save(any(TeamRegistration.class));
    }

    @Test
    public void testGetTournamentById_Success() {
        Long tournamentId = 1L;
        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        TournamentDTO result = tournamentService.getTournamentById(tournamentId);

        assertNotNull(result);
        assertEquals(tournamentId, result.getTournamentDTOId());
    }

    @Test
    public void testGetCricketMatchById_Success() {
        Long matchId = 1L;
        CricketMatch cricketMatch = new CricketMatch();
        cricketMatch.setId(matchId);

        when(cricketMatchRepository.findById(matchId)).thenReturn(Optional.of(cricketMatch));

        MatchDetailsDTO result = tournamentService.getCricketMatchById(matchId);

        assertNotNull(result);
        assertEquals(matchId, result.getMatchId());
    }

    @Test
    public void testGetAllMatches_Success() {
        CricketMatch cricketMatch = new CricketMatch();
        cricketMatch.setId(1L);

        List<CricketMatch> matches = Collections.singletonList(cricketMatch);
        when(cricketMatchRepository.findAll()).thenReturn(matches);

        List<MatchDetailsDTO> result = tournamentService.getAllMatches();

        assertNotNull(result);
        assertEquals(1, result.size());
        MatchDetailsDTO dto = result.get(0);
        assertEquals(1L, dto.getMatchId());
    }

    @Test
    public void testGetMatchDetails_Success() {
        Long matchId = 1L;
        Innings innings = new Innings();
        innings.setId(1L);
        innings.setInningsNumber(1L);

        Ball ball = new Ball();
        ball.setId(1L);
        ball.setInnings(innings);

        List<Innings> inningsList = Collections.singletonList(innings);
        List<Ball> balls = Collections.singletonList(ball);

        when(inningsRepository.findByCricketMatchId(matchId)).thenReturn(inningsList);
        when(ballRepository.findByInningsId(innings.getId())).thenReturn(balls);

        MatchResponseDTO result = tournamentService.getMatchDetails(matchId);

        assertNotNull(result);
        assertEquals(matchId, result.getMatchId());
        assertEquals(1, result.getInnings().size());
    }
}

