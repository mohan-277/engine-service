package com.mohan.gameengineservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Player {

//    String name;
//    String dateOfBirth;
//    String specialization;
//    String gender;
//    int playedMatches;
//    long runs;
//    int highScore;
//    double strikeRate;
//    int numberOf50s;
//    int numberOf100s;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate dateOfBirth;
    private String specialization;
    private String gender;
    private String country;

    private Integer playedMatches;
    private Long runs;
    private Long wickets;
    private Integer highScore;
    private Double strikeRate;
    private Integer numberOf50s;
    private Integer numberOf100s;


    @Lob
    @Column(name = "profile_picture", columnDefinition="LONGBLOB")
    private byte[] profilePicture;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonIgnore
    private Team team;

    private boolean isOut;
}
