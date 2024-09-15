package com.sbear.gameengineservice.mappers;

import com.sbear.gameengineservice.dto.TeamDTO;
import com.sbear.gameengineservice.entity.Team;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeamMapper {

    public static TeamDTO toDTO(Team team) {
        if (team == null) {
            return null;
        }

        return TeamDTO.builder()
                .name(team.getName())
                .country(team.getCountry())
                .teamCaptain(team.getTeamCaptain())
                .coach(team.getCoachName())
                .owner(team.getOwner())
                .players(team.getPlayers() == null ? null : team.getPlayers().stream()
                        .map(PlayerMapper::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    public static List<TeamDTO> toDTOList(List<Team> teams) {
        return teams.stream()
                .map(TeamMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Team toEntity(TeamDTO teamDTO) {
        if (teamDTO == null) {
            return null;
        }

        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setCountry(teamDTO.getCountry());
        team.setTeamCaptain(teamDTO.getTeamCaptain());
        team.setOwner(teamDTO.getOwner());
        team.setCoachName(teamDTO.getCoach());


        return team;
    }


}
