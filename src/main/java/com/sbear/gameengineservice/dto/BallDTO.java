package com.sbear.gameengineservice.dto;

import com.sbear.gameengineservice.entity.constants.BallType;
import com.sbear.gameengineservice.entity.constants.WicketType;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class BallDTO {
    private Long ballId;
    private Long InningId;
    private Integer overNumber;
    private Integer ballNumber;
    private BallType ballType;
    private String striker;
    private String nonStriker;
    private String bowler;
    private Integer runsScored;
    private WicketType wicket;
    private Integer totalScore;
    private Integer wicketNumber;


}
