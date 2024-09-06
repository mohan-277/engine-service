package com.mohan.gameengineservice.service.impl;

import com.mohan.gameengineservice.dto.PlayerDTO;
import com.mohan.gameengineservice.dto.TeamDTO;
import com.mohan.gameengineservice.dto.TeamSummary;
import com.mohan.gameengineservice.entity.Player;
import com.mohan.gameengineservice.entity.Team;
import com.mohan.gameengineservice.exceptions.ResourceNotFoundException;
import com.mohan.gameengineservice.repository.PlayerRepository;
import com.mohan.gameengineservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;



@Service
public class TeamService {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    PlayerRepository playerRepository;


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
