package com.mohan.gameengineservice.entity;

import com.mohan.gameengineservice.entity.constants.BallType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class Ball {

    @Id
    private  long id;
    private  BallType ballType;
    private  double ballSpeed;
    @OneToOne
    private  PlayerObject playedBy;
    @OneToOne
    private  PlayerObject bowledBy;
    private  int run;
    @OneToOne
    private  Wicket wicket;

    public static void main(String[] args) {
        Over over = new Over();
        List<Ball>  balls = new ArrayList<>(6);
        Ball ball = new Ball();
        ball.ballType= BallType.WIDE;
        ball.ballSpeed = 1.0;
        ball.playedBy = new PlayerObject();
        ball.bowledBy = new PlayerObject();
        ball.run = 1;
        ball.wicket = new Wicket();
        balls.add(ball);
        over.setBalls(balls);
        System.out.println(over);
    }
}

