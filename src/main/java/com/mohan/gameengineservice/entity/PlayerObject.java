package com.mohan.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class PlayerObject {
    @Id
    private long id;
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
    @ManyToMany
    private List<Over> overs;
    @ManyToMany
    private List<Wicket> wickets;
    @ManyToMany
    private List<Ball> balls;


    private int score; // Runs scored by the player
    private int ballsFaced; // Balls faced by the player
    private int fours; // Fours hit by the player
    private int sixes; // Sixes hit by the player

}
