package com.sbear.gameengineservice.service.impl;

import com.sbear.gameengineservice.dto.PlayerDTO;
import com.sbear.gameengineservice.dto.TeamDTO;
import com.sbear.gameengineservice.dto.TeamSummary;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.Team;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import com.sbear.gameengineservice.repository.PlayerRepository;
import com.sbear.gameengineservice.repository.StatusOfMatchRepository;
import com.sbear.gameengineservice.repository.TeamRepository;
import com.sbear.gameengineservice.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;



@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    StatusOfMatchRepository statusOfMatchRepository;

    @Transactional
    public void setPlayersForTeam(Long teamId, List<PlayerDTO> playerDTOs) {
        // Retrieve the Team entity
        Team team = teamRepository.findById(Math.toIntExact(teamId))
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        // Clear existing players
        team.clearPlayers(); // Method in Team class to handle removal

        // Add new players
        for (PlayerDTO playerDTO : playerDTOs) {
            Player player = playerRepository.findById(playerDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Player not found"));

            // Add player to the team
            team.addPlayer(player); // Method in Team class to handle addition
        }

        // Save the updated team
        teamRepository.save(team);
    }


    public TeamSummary getTeamSummary(Long coachId) {
        TeamSummary teamSummary = teamRepository.findTeamSummaryByCoachId(coachId);
        if (teamSummary == null) {
            // Handle the case where the coachId does not exist
            throw new ResourceNotFoundException("Coach ID " + coachId + " does not exist. Please provide a valid coach ID.");
        }
        return teamSummary;

    }



    public TeamDTO convertToTeamDto(Team team) {
        List<PlayerDTO> playerDTOList = team.getPlayers().stream()
                .map(this::convertToPlayerDto)
                .collect(Collectors.toList());

        // Use TeamDTOBuilder to build the TeamDTO
        return TeamDTO.builder()
                .name(team.getName())
                .country(team.getCountry())
                .teamCaptain(team.getTeamCaptain())
                .coach(team.getCoachName())
                .owner(team.getOwner())
                .players(playerDTOList)
                .build();
    }


    private PlayerDTO convertToPlayerDto(Player player) {
        return PlayerDTO.builder()
                .id(player.getId())
                .name(player.getName())
                .dateOfBirth(player.getDateOfBirth().toString()) // Convert LocalDate to String
                .specialization(player.getSpecialization())
                .gender(player.getGender())
                .country(player.getCountry())
                .playedMatches(player.getPlayedMatches())
                .runs(player.getRuns())
                .wickets(player.getWickets())
                .highScore(player.getHighScore())
                .profilePicture(player.getProfilePicture())
                .build();
    }


//    public TeamDTO convertToTeamDto(Team team) {
//        TeamDTO teamDTO = new TeamDTO();
//        teamDTO.setName(team.getName());
//        teamDTO.setTeamCaptain(team.getTeamCaptain());
//        teamDTO.setCoach(team.getCoach());
//        teamDTO.setCountry(team.getCountry());
//        teamDTO.setOwner(team.getOwner());
//        List<PlayerDTO> playerDTOList = new ArrayList<>();
//        for (Player player : team.getPlayers()) {
//            PlayerDTO playerDTO = getPlayerDTO(player);
//            playerDTOList.add(playerDTO);
//        }
//        teamDTO.setPlayers(playerDTOList);
//        return teamDTO;
//    }
//
//    public static PlayerDTO getPlayerDTO(Player player) {
//        PlayerDTO playerDTO = new PlayerDTO();
//        playerDTO.setId(player.getId());
//        playerDTO.setName(player.getName());
//        playerDTO.setCountry(player.getCountry());
//        playerDTO.setRuns(player.getRuns());
//        playerDTO.setWickets(player.getWickets());
//        playerDTO.setGender(player.getGender());
//        playerDTO.setDateOfBirth(player.getDateOfBirth().toString());
//        playerDTO.setHighScore(player.getHighScore());
//        playerDTO.setNumberOf50s(player.getNumberOf50s());
//        playerDTO.setNumberOf100s(player.getNumberOf100s());
//        playerDTO.setPlayedMatches(player.getPlayedMatches());
//        return playerDTO;
//    }

    public  Long getTheCountOfTheStagesStarted(String matchStageName){
        return statusOfMatchRepository.findTopCountByNameOrderByCountDesc(matchStageName);
    }



    //    public Team createTeam(TeamDTO teamDTO) {
//        Team team = new Team();
//        team.setName(teamDTO.getName());
//        team.setCountry(teamDTO.getCountry());
//
//        List<Player> players = teamDTO.getPlayers().stream().map(dto -> {
//            Player player = new Player();
//            player.setName(dto.getName());
//            player.setDateOfBirth(dto.getDateOfBirth());
//            player.setSpecialization(dto.getSpecialization());
//            player.setGender(dto.getGender());
//            player.setCountry(dto.getCountry());
//            return player;
//        }).collect(Collectors.toList());
//
//        team.setPlayers(players);
//
//        return teamRepository.save(team);
//    }


}
