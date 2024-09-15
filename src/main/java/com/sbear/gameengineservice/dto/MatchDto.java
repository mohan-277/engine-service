package com.sbear.gameengineservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchDto {
    private Long id;
    private String teamA;
    private String teamB;
    private String currentScore;
    private String result;
    private boolean isLive;
}
