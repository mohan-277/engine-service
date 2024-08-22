package com.mohan.gameengineservice.entity;

import com.mohan.gameengineservice.entity.constants.MatchType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Primary;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Entity
@Table(name = "cricket_matches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CricketMatch {

//    List<Innings> list;
//    MatchType type;
//    Date date;
//    Team A;
//    Team B;
//    Time startTime;
//    Time endTime;
//    Location location;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String stadiumName;
    private String matchType; // e.g., "T20", "ODI", "Test"
    private String currentPlayingTeam;
    private String currentScore;
    private String currentWickets;
    private String currentOvers;
    private String chaseText; // e.g., "111 runs needed to win in 60 balls"
    private String secondTeamName;
    private String secondTeamScore;
    private String secondTeamWickets;
    private String secondTeamOvers;

    private LocalDateTime startTime; // Match start time
    private LocalDateTime endTime;
}
