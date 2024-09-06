package com.mohan.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Team {

     @Id
     @Column(name = "team_id")
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long teamId;

     @Column(name = "coach_id")
     private Long coachId;


     private  String name;
     private  String country;
     private String teamCaptain;


     private String coach;

     private String owner;


     @ManyToOne
     @JoinColumn(name = "tournament_id")
     private Tournament tournament;


     @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
     private List<Player> players = new ArrayList<>();    // total 15 player 5 bowlers, 5 batsmen, 5 all-rounder this is simple

     private int totalPoints;

     @Lob
     @Column(name = "logo")
     private byte[] logo; // For storing the team's logo this is for the landing page of the teams
     // according to the requirement we will use
     private String icon; // For storing a text or URL for the team's icon  this for the icon on the matches



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
}
