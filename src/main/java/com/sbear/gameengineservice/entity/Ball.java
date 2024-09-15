package com.sbear.gameengineservice.entity;

import com.sbear.gameengineservice.entity.constants.BallType;
import com.sbear.gameengineservice.entity.constants.WicketType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
@Table(name = "ball")
public class Ball {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Column(name = "match_id")
    private Long matchId;
    @Column(name = "ball_type")
    private  BallType ballType;
    @Column(name = "over_number")
    private Integer overNumber;
    private String strikerName;
    @Column(name = "non_striker_name")
    private String nonStrikerName;

    @Column(name = "bowler_name")
    private String bowlerName;
    private Integer ballNumber;
    private  Integer runsScored;
    @OneToOne
    private  Wicket wicket;

    @Column(name = "wicket_type")
    private WicketType wicketType;

    private Integer totalScore; // This is for calculation purposes, not persisted

    private Integer wicketCount;


    @Column(name = "balls_bowled")
    private Integer ballsBowled;

    @Column(name = "wickets_taken")
    private Integer wicketsTaken;


    @ManyToOne
    @JoinColumn(name = "innings_id")
    private Innings innings;

//    @OneToOne
//    private  PlayerObject playedBy;
//    @OneToOne
//    private  PlayerObject bowledBy;
//    @ManyToOne
//    private Over over;

    public static void main(String[] args) {
        Over over = new Over();
        List<Ball>  balls = new ArrayList<>(6);
        Ball ball = new Ball();
        ball.ballType= BallType.WIDE;
//        ball.runs = 1;
        ball.wicket = new Wicket();
        balls.add(ball);
        over.setBalls(balls);
        System.out.println(over);
    }

    public void setPlayer(Player bowler) {

    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

