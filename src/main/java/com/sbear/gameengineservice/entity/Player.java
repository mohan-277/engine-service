package com.sbear.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long id;
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private String specialization;
    private String gender;
    private String country;
    private String userName;
    private String role;
    private Integer playedMatches;
    // Getters and Setters for new fields
    @Setter
    @Getter
    private Long runs;
    @Getter
    @Setter
    private Long wickets;
    private Integer highScore =0;
    private Integer ballsFaced = 0;
    private Integer fours = 0;
    private Integer sixes = 0;
    private Integer dotBalls = 0;
    private Integer singles = 0;
    private Integer twos = 0;
    private Integer threes = 0;
    private Integer oversBowled = 0;
    private Integer wicketsTaken = 0;
    @Column(name = "is_playing")
    private Boolean isPlaying;

    @Column(name = "is_overseas")
    private Boolean isOverseas;

    @Column(name = "is_backup")
    private Boolean isBackup;

    @Lob
    @Column(name = "profile_picture", columnDefinition="LONGBLOB")
    private byte[] profilePicture;

    @Lazy
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "is_out", nullable = true)
    private Boolean isOut;

    // Constructor
    public Player(String name, LocalDate of, String specialization, String gender, String country) {
        this.name = name;
        this.dateOfBirth = of;
        this.specialization = specialization;
        this.gender = gender;
        this.country = country;
    }

    // Default constructor
    public Player() {}


    // Other existing getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getPlayedMatches() {
        return playedMatches;
    }

    public void setPlayedMatches(Integer playedMatches) {
        this.playedMatches = playedMatches;
    }

    public Integer getHighScore() {
        return highScore;
    }

    public void setHighScore(Integer highScore) {
        this.highScore = highScore;
    }


    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean isOut() {
        return isOut;
    }

    public void setOut(boolean isOut) {
        this.isOut = isOut;
    }
}
