package com.sbear.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_registration")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;


    private String groupType;
    private String coachName;

    private LocalDateTime registrationDate;

    public TeamRegistration(Team team, String groupType) {
        this.team = team;
        this.groupType = groupType;
    }
}
