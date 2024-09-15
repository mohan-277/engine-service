package com.sbear.gameengineservice.utilities;

import java.time.LocalDate;

public class PlayerUtil {
    private String name;
    private LocalDate dateOfBirth;
    private String specialization;
    private String gender;
    private String country;
    private Integer playedMatches = 0;
    private Long runs = 0L;
    private Long wickets = 0L;
    private Integer highScore = 0;
    private Double strikeRate = 0.0;
    private Integer numberOf50s = 0;
    private Integer numberOf100s = 0;
    private boolean isOut;

    public PlayerUtil() {
    }

    public PlayerUtil(String s, LocalDate of, String specialization, String male, String countryA) {
        this.specialization=specialization;
        this.name = s;
    }

    public String getName() {
        return name;
    }

    public PlayerUtil(String name, LocalDate dateOfBirth, String specialization, String gender, String country, Integer playedMatches, Long runs, Long wickets, Integer highScore, Double strikeRate, Integer numberOf50s, Integer numberOf100s, boolean isOut) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.specialization = specialization;
        this.gender = gender;
        this.country = country;
        this.playedMatches = playedMatches;
        this.runs = runs;
        this.wickets = wickets;
        this.highScore = highScore;
        this.strikeRate = strikeRate;
        this.numberOf50s = numberOf50s;
        this.numberOf100s = numberOf100s;
        this.isOut = isOut;
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

    public boolean isOut() {
        return isOut;
    }

    public void setOut(boolean out) {
        isOut = out;
    }
}
