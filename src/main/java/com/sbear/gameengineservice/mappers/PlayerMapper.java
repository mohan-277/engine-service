package com.sbear.gameengineservice.mappers;

import com.sbear.gameengineservice.dto.PlayerDTO;
import com.sbear.gameengineservice.entity.Player;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PlayerMapper {

    public static PlayerDTO toDTO(Player player) {
        if (player == null) {
            return null;
        }
        String dateOfBirth = player.getDateOfBirth() != null
                ? player.getDateOfBirth().format(DateTimeFormatter.ISO_LOCAL_DATE)
                : null;
        return PlayerDTO.builder()
                .id(player.getId())
                .name(player.getName())
                .dateOfBirth(dateOfBirth)
                .specialization(player.getSpecialization())
                .gender(player.getGender())
                .country(player.getCountry())
                .playedMatches(player.getPlayedMatches())
                .runs(player.getRuns())
                .wickets(player.getWickets())
                .highScore(player.getHighScore())
                .build();
    }

    public static Player toEntity(PlayerDTO dto) {
        if (dto == null) {
            return null;
        }

        return Player.builder()
                .id(dto.getId())
                .name(dto.getName())
                .dateOfBirth(LocalDate.parse(dto.getDateOfBirth()))
                .specialization(dto.getSpecialization())
                .gender(dto.getGender())
                .country(dto.getCountry())
                .playedMatches(dto.getPlayedMatches())
                .runs(dto.getRuns())
                .wickets(dto.getWickets())
                .highScore(dto.getHighScore())
                .build();
    }
}
