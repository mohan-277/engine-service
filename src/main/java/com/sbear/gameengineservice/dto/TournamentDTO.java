package com.sbear.gameengineservice.dto;

import com.sbear.gameengineservice.entity.constants.TournamentStatus;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;


@NoArgsConstructor
@Data
@AllArgsConstructor
public class TournamentDTO {
    private Long  tournamentDTOId ;
    private String tournamentName;
    private String tournamentType;
    private String location;
    private String startDate;
    private Duration matchInterval;
    private Integer numberOfTeams; // Number of teams participating
    private Integer registeredTeamsCount;
    private TournamentStatus status; // planned , ongoing , started


    public TournamentDTO(Long id, String tournamentName, String tournamentType, String location, LocalDateTime startDate, Duration matchInterval, int numberOfTeams, int registeredTeamsCount, TournamentStatus status) {
        this.tournamentDTOId = id;
        this.tournamentName = tournamentName;
        this.tournamentType = tournamentType;
        this.location = location;
        this.startDate = startDate.toString();
        this.matchInterval = matchInterval;
        this.numberOfTeams = numberOfTeams;
        this.registeredTeamsCount = registeredTeamsCount;
        this.status = status;

    }
}
