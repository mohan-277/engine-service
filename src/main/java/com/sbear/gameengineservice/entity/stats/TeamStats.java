package com.sbear.gameengineservice.entity.stats;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "team-stats")
public class TeamStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier (auto-generated)
    
    private Long matchId; // Match ID
    private String teamName;
    
    private Integer runs;
    
    private Integer wickets;
    
    private Integer oversCompleted;
    
    private Integer ballsInCurrentOver;
    
    private Integer teamScore;
    private String result;
    
    private Integer points;

    private Integer matchesPlayed;

    // Match details
    private String location;
    private String matchType;
    private String matchStage;
    private String matchGroup;
    private LocalDateTime matchDateTime;
    private boolean live;
    private String status;
    
    private Integer numberOfWins;
    
    private Integer numberOfLosses;

    public TeamStats(String teamName, int runs) {
        this.teamName = teamName;
        this.runs = runs;
    }
}
