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

    private Integer totalScore;

    private Integer wicketCount;


    @Column(name = "balls_bowled")
    private Integer ballsBowled;

    @Column(name = "wickets_taken")
    private Integer wicketsTaken;


    @ManyToOne
    @JoinColumn(name = "innings_id")
    private Innings innings;


}

