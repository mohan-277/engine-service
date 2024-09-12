package com.mohan.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cricket_matches_test")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CricketMatchesTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tournament_id")
    private Long tournament_id;

}
