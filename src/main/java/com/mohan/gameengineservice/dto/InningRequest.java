package com.mohan.gameengineservice.dto;


import com.mohan.gameengineservice.entity.Team;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class InningRequest {
    private Team battingTeam;
    private Team bowlingTeam;
}

