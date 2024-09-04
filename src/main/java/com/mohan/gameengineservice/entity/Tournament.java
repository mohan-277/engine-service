package com.mohan.gameengineservice.entity;

import com.mohan.gameengineservice.entity.constants.TournamentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tournaments")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tournamentName;
    private String location;
    private LocalDateTime startDate;
    private Duration matchInterval;
    private int numberOfTeams;


    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamRegistration> teamRegistrations = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CricketMatch> matches = new ArrayList<>();


    private TournamentStatus status;
}
