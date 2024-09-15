package com.sbear.gameengineservice.service.impl;

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
import com.sbear.gameengineservice.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    private final PlayerRepository playerRepository;

   private final StatusOfMatchRepository statusOfMatchRepository;

   private final TeamMapper teamMapper;


    public TeamServiceImpl(TeamRepository teamRepository, PlayerRepository playerRepository, StatusOfMatchRepository statusOfMatchRepository, TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.statusOfMatchRepository = statusOfMatchRepository;
        this.teamMapper = teamMapper;
    }

    @Transactional
    public void setPlayersForTeam(Integer teamId, List<PlayerDTO> playerDTOs) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        team.clearPlayers();

        for (PlayerDTO playerDTO : playerDTOs) {
            Player player = PlayerMapper.toEntity(playerDTO);
            if (playerRepository.existsById(player.getId())) {
                team.addPlayer(player);
            } else {
                throw new EntityNotFoundException("Player with ID " + player.getId() + " not found");
            }
        }

        teamRepository.save(team);
    }


    public TeamSummary getTeamSummary(Long coachId) {
        TeamSummary teamSummary = teamRepository.findTeamSummaryByCoachId(coachId);
        if (teamSummary == null) {

            throw new ResourceNotFoundException("Coach ID " + coachId + " does not exist. Please provide a valid coach ID.");
        }
        return teamSummary;

    }

    public TeamDTO getTeamById(Integer teamId){
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        return TeamMapper.toDTO(team);
    }

    @Transactional
    public void createTeam(TeamDTO teamDTO) {
        Team team = teamMapper.toEntity(teamDTO); // Use TeamMapper for conversion

        // Handle players
        List<Player> players = teamDTO.getPlayers().stream().map(playerDTO -> {
            Optional<Player> existingPlayerOpt = playerRepository.findByNameAndDateOfBirth(playerDTO.getName(), LocalDate.parse(playerDTO.getDateOfBirth()));
            Player player = existingPlayerOpt.orElseGet(() -> createNewPlayer(playerDTO));
            player.setTeam(team);
            return player;
        }).collect(Collectors.toList());
        team.setCoachId(generateNextCoachId());
        team.setPlayers(players);
        teamRepository.save(team);
    }

    private Player createNewPlayer(PlayerDTO playerDTO) {
        Player player = PlayerMapper.toEntity(playerDTO); // Using PlayerMapper for conversion
        return playerRepository.save(player);
    }

    private Long generateNextCoachId() {
        Long maxCoachId = teamRepository.findMaxCoachId();
        return (maxCoachId == null) ? 1L : maxCoachId + 1;
    }

    public  Long getTheCountOfTheStagesStarted(String matchStageName){
        return statusOfMatchRepository.findTopCountByNameOrderByCountDesc(matchStageName);
    }

    public List<TeamDTO> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        return TeamMapper.toDTOList(teams);
    }

    public List<TeamSummary> getAllTeamSummaries(){
        return teamRepository.findAllTeamSummaries();
    }


}
