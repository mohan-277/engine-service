package com.sbear.gameengineservice.dto;

import com.sbear.gameengineservice.entity.constants.BallType;
import com.sbear.gameengineservice.entity.constants.WicketType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
