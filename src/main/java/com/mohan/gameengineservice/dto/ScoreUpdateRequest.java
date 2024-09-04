package com.mohan.gameengineservice.dto;

import com.mohan.gameengineservice.entity.Player;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class ScoreUpdateRequest {

    private Player player;
    private int runs;
    private int ballsFaced;
    private boolean isOut;
}
