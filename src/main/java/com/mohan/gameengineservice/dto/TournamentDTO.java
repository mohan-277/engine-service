package com.mohan.gameengineservice.dto;

import com.mohan.gameengineservice.entity.constants.TournamentStatus;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class TournamentDTO {
    private Long  tournamentDTOId ;
    private String name;
    private String location;
    private LocalDateTime startDate;
    private Duration matchInterval;
    private Integer numberOfTeams; // Number of teams participating
    private Integer registeredTeamsCount;
    private TournamentStatus status; // planned , ongoing , started


}
