package com.sbear.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@Table(name = "team")
public class Team {

     @Id
     @Column(name = "team_id")
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long teamId;

     @Column(name = "coach_id")
     private Long coachId;


     @Setter
     @Getter
     private  String name;
     @Setter
     @Getter
     private  String country;
     private String teamCaptain;

     private String coachName;

     private String owner;

     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "tournament_id")
     private Tournament tournament;

    @Setter
    @Getter
    @Column(name = "team_players")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Player> players = new ArrayList<>();    // total 15 player 5 bowlers, 5 batsmen, 5 all-rounder this is simple

     private int totalPoints;

     @Lob
     @Column(name = "logo")
     private byte[] logo; // For storing the team's logo this is for the landing page of the teams


     // according to the requirement we will use
     private String icon; // For storing a text or URL for the team's icon  this for the icon on the matches


    @Setter
    private Long runsScored = 0L;
    private Integer wicketsLost = 0;
    private Double oversPlayed = 0.0;



    public Team(String teamA) {
        this.name = teamA;
    }


    public void clearPlayers() {
          for (Player player : new ArrayList<>(players)) {
               removePlayer(player);
          }
     }

     public void removePlayer(Player player) {
          players.remove(player);
          player.setTeam(null); // Remove the bidirectional reference
     }

     public void addPlayer(Player player) {
          players.add(player);
          player.setTeam(this); // Bidirectional reference
     }

    public Team(String name, String country) {
        this.name = name;
        this.country = country;
    }

    // Default constructor
    public Team() {}



    public void setWicketsLost(Integer wicketsLost) {
        this.wicketsLost = wicketsLost;
    }

    public Double getOversPlayed() {
        return oversPlayed;
    }

    public void setOversPlayed(Double oversPlayed) {
        this.oversPlayed = oversPlayed;
    }

    // Other existing getters and setters

    public Long getId() {
        return teamId;
    }

    public void setId(Long id) {
        this.teamId = id;
    }

}
