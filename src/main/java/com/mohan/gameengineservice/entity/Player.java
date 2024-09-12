package com.mohan.gameengineservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@AllArgsConstructor
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


    private Integer ballsFaced = 0;
    private Integer fours = 0;
    private Integer sixes = 0;
    private Integer dotBalls = 0;
    private Integer singles = 0;
    private Integer twos = 0;
    private Integer threes = 0;
    private Integer oversBowled = 0;
    private Integer wicketsTaken = 0;

    @Lob
    @Column(name = "profile_picture", columnDefinition="LONGBLOB")
    private byte[] profilePicture;

    @Lazy
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private boolean isOut;

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

    // Getters and Setters for new fields
    public Long getRuns() {
        return runs;
    }

    public void setRuns(Long runs) {
        this.runs = runs;
    }

    public Long getWickets() {
        return wickets;
    }

    public void setWickets(Long wickets) {
        this.wickets = wickets;
    }

    public Integer getBallsFaced() {
        return ballsFaced;
    }

    public void setBallsFaced(Integer ballsFaced) {
        this.ballsFaced = ballsFaced;
    }

    public Integer getFours() {
        return fours;
    }

    public void setFours(Integer fours) {
        this.fours = fours;
    }

    public Integer getSixes() {
        return sixes;
    }

    public void setSixes(Integer sixes) {
        this.sixes = sixes;
    }

    public Integer getDotBalls() {
        return dotBalls;
    }

    public void setDotBalls(Integer dotBalls) {
        this.dotBalls = dotBalls;
    }

    public Integer getSingles() {
        return singles;
    }

    public void setSingles(Integer singles) {
        this.singles = singles;
    }

    public Integer getTwos() {
        return twos;
    }

    public void setTwos(Integer twos) {
        this.twos = twos;
    }

    public Integer getThrees() {
        return threes;
    }

    public void setThrees(Integer threes) {
        this.threes = threes;
    }

    public Integer getOversBowled() {
        return oversBowled;
    }

    public void setOversBowled(Integer oversBowled) {
        this.oversBowled = oversBowled;
    }

    public Integer getWicketsTaken() {
        return wicketsTaken;
    }

    public void setWicketsTaken(Integer wicketsTaken) {
        this.wicketsTaken = wicketsTaken;
    }

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

    public Double getStrikeRate() {
        return strikeRate;
    }

    public void setStrikeRate(Double strikeRate) {
        this.strikeRate = strikeRate;
    }

    public Integer getNumberOf50s() {
        return numberOf50s;
    }

    public void setNumberOf50s(Integer numberOf50s) {
        this.numberOf50s = numberOf50s;
    }

    public Integer getNumberOf100s() {
        return numberOf100s;
    }

    public void setNumberOf100s(Integer numberOf100s) {
        this.numberOf100s = numberOf100s;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
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
