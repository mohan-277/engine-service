package com.mohan.gameengineservice.service.impl;

import com.mohan.gameengineservice.dto.TeamDTO;
import com.mohan.gameengineservice.entity.Player;
import com.mohan.gameengineservice.entity.Team;
import com.mohan.gameengineservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;



@Service
public class TeamService {

    @Autowired
    TeamRepository teamRepository;


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
