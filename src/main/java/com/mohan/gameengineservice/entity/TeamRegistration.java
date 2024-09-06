package com.mohan.gameengineservice.entity;

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

//    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "coach_id")
//    private Coach coach;

    private String groupType;
    private String coachName;

    private LocalDateTime registrationDate;
}
