package com.sbear.gameengineservice.entity.stats;

import com.sbear.gameengineservice.entity.Player;
import jakarta.persistence.*;

@Table(name = "player_score_card")
public class PlayerScoreCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_card_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "player_id")
    private Player player;

    private Long runs;
    private Long wickets;
    private Integer highScore = 0;
    private Integer ballsFaced = 0;
    private Integer fours = 0;
    private Integer sixes = 0;
    private Integer dotBalls = 0;
    private Integer singles = 0;
    private Integer twos = 0;
    private Integer threes = 0;
    private Integer oversBowled = 0;
    private Integer wicketsTaken = 0;
}
