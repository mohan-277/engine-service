package com.sbear.gameengineservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sbear.gameengineservice.dto.PlayerDTO;
import com.sbear.gameengineservice.dto.TeamDTO;
import com.sbear.gameengineservice.dto.TeamSummary;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.Team;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import com.sbear.gameengineservice.mappers.PlayerMapper;
import com.sbear.gameengineservice.mappers.TeamMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private StatusOfMatchRepository statusOfMatchRepository;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamServiceImpl teamService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testSetPlayersForTeam_TeamNotFound() {
        Integer teamId = 1;
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(1L);
        List<PlayerDTO> playerDTOs = Collections.singletonList(playerDTO);

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> teamService.setPlayersForTeam(teamId, playerDTOs));
        assertEquals("Team not found", thrown.getMessage());
    }

    @Test
    @Transactional
    public void testSetPlayersForTeam_Success() {
        Integer teamId = 1;
        Team team = new Team();
        team.setId(Long.valueOf(teamId)); // Ensure the team has an ID

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(1L);
        playerDTO.setName("Player Name");
        playerDTO.setDateOfBirth("2000-01-01");

        List<PlayerDTO> playerDTOs = Collections.singletonList(playerDTO);

        // Mocking repository responses
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(playerRepository.existsById(playerDTO.getId())).thenReturn(true);

        // Mocking PlayerMapper conversion
        Player mockPlayer = new Player();
        mockPlayer.setId(playerDTO.getId());
        when(PlayerMapper.toEntity(playerDTO)).thenReturn(mockPlayer);

        // Call the service method
        teamService.setPlayersForTeam(teamId, playerDTOs);

        // Verify interactions with mock repositories
        verify(teamRepository).findById(teamId);
        verify(playerRepository).existsById(playerDTO.getId());
        verify(teamRepository).save(team);

        // Verify that the player was added to the team
        assertTrue(team.getPlayers().contains(mockPlayer), "The player should be added to the team");
    }
    @Test
    public void testGetTeamSummary_Success() {
        Long coachId = 1L;

        // Create a mock of TeamSummary interface
        TeamSummary mockTeamSummary = mock(TeamSummary.class);

        // Stub the repository method to return the mock TeamSummary
        when(teamRepository.findTeamSummaryByCoachId(coachId)).thenReturn(mockTeamSummary);

        // Call the method to test
        TeamSummary result = teamService.getTeamSummary(coachId);

        // Verify the result
        assertNotNull(result);
        assertEquals(mockTeamSummary, result);

        // Verify that the repository method was called with the correct parameter
        verify(teamRepository).findTeamSummaryByCoachId(coachId);
    }

    @Test
    public void testGetTeamSummary_NotFound() {
        Long coachId = 1L;

        when(teamRepository.findTeamSummaryByCoachId(coachId)).thenReturn(null);

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> teamService.getTeamSummary(coachId));
        assertEquals("Coach ID 1 does not exist. Please provide a valid coach ID.", thrown.getMessage());
    }

    @Test
    public void testGetTeamById_Success() {
        Integer teamId = 1;
        Team team = new Team();
        TeamDTO teamDTO = new TeamDTO();

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamMapper.toDTO(team)).thenReturn(teamDTO);

        TeamDTO result = teamService.getTeamById(teamId);

        assertNotNull(result);
        assertEquals(teamDTO, result);
    }

    @Test
    public void testGetTeamById_NotFound() {
        Integer teamId = 1;

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> teamService.getTeamById(teamId));
        assertEquals("Team not found", thrown.getMessage());
    }

    @Test
    @Transactional
    public void testCreateTeam_Success() {
        TeamDTO teamDTO = new TeamDTO();
        Team team = new Team();
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setName("Player 1");
        playerDTO.setDateOfBirth("2000-01-01");

        when(teamMapper.toEntity(teamDTO)).thenReturn(team);
        when(playerRepository.findByNameAndDateOfBirth(playerDTO.getName(), LocalDate.parse(playerDTO.getDateOfBirth())))
                .thenReturn(Optional.empty());
        when(playerRepository.save(any(Player.class))).thenReturn(new Player());
        when(teamRepository.save(team)).thenReturn(team);
        when(teamRepository.findMaxCoachId()).thenReturn(0L);

        teamService.createTeam(teamDTO);

        verify(teamRepository).save(team);
    }

    @Test
    public void testGetTheCountOfTheStagesStarted_Success() {
        String matchStageName = "Group Stage";
        Long count = 5L;

        when(statusOfMatchRepository.findTopCountByNameOrderByCountDesc(matchStageName)).thenReturn(count);

        Long result = teamService.getTheCountOfTheStagesStarted(matchStageName);

        assertNotNull(result);
        assertEquals(count, result);
    }

    @Test
    public void testGetAllTeams_Success() {
        List<Team> teams = Collections.singletonList(new Team());
        List<TeamDTO> teamDTOs = Collections.singletonList(new TeamDTO());

        when(teamRepository.findAll()).thenReturn(teams);
        when(teamMapper.toDTOList(teams)).thenReturn(teamDTOs);

        List<TeamDTO> result = teamService.getAllTeams();

        assertNotNull(result);
        assertEquals(teamDTOs, result);
    }

    @Test
    public void testGetAllTeamSummaries_Success() {
        // Create a mock of the TeamSummary interface
        TeamSummary mockTeamSummary = mock(TeamSummary.class);

        // Create a list with the mocked TeamSummary
        List<TeamSummary> summaries = Collections.singletonList(mockTeamSummary);

        // Stub the repository method to return the list of mocks
        when(teamRepository.findAllTeamSummaries()).thenReturn(summaries);

        // Call the service method
        List<TeamSummary> result = teamService.getAllTeamSummaries();

        // Verify the result
        assertNotNull(result);
        assertEquals(summaries, result);

        // Verify that the repository method was called
        verify(teamRepository).findAllTeamSummaries();
    }
}
