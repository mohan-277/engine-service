package com.sbear.gameengineservice.dto;


import com.sbear.gameengineservice.entity.Team;
import lombok.*;


@Setter
@Getter
@Builder
public class InningRequest {
    private Team battingTeam;
    private Team bowlingTeam;
}

