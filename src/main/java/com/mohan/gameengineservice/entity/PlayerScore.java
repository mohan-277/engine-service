package com.mohan.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "inning_id")
    private Innings inning;

    private int runsScored;
    private int ballsFaced;
    private int fours;
    private int sixes;
    private boolean isOut;


}
