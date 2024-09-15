package com.sbear.gameengineservice.entity;

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
    private PlayerObject player;

    @ManyToOne
    @JoinColumn(name = "innings_id")
    private Innings innings;

    private int runs;
    private int ballsFaced;
    private int fours;
    private int sixes;
    private int dotBalls;
    private int singles;
    private int twos;
    private int threes;

    private int oversBowled;
    private int wicketsTaken;

}
