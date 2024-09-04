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
    private int score;

}
