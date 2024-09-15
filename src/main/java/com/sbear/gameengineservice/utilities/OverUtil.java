package com.sbear.gameengineservice.utilities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class OverUtil {
    private long id;
    private int number;
    private List<BallUtil> balls;

    public OverUtil(long id) {
        this.id = id;
        this.balls = new ArrayList<>();
    }

}
