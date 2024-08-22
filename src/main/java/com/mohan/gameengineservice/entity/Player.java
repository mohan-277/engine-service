package com.mohan.gameengineservice.entity;

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

    private int playedMatches;
    private long runs;
    private int highScore;
    private double strikeRate;
    private int numberOf50s;
    private int numberOf100s;


    @Lob
    @Column(name = "profile_picture", columnDefinition="LONGBLOB")
    private byte[] profilePicture;
}
