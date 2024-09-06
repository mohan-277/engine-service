package com.mohan.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coaches")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String username;
    private String password;

//    @ManyToOne
//    @JoinColumn(name = "team_id")
//    private Team team;

//    @OneToMany
//    @JoinTable(
//            name = "coach_tournament_registration",
//            joinColumns = @JoinColumn(name = "coach_id"),
//            inverseJoinColumns = @JoinColumn(name = "tournament_id")
//    )
//    private List<Tournament> tournaments = new ArrayList<>();


}
