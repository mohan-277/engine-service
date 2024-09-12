package com.mohan.gameengineservice.utilities;

import java.util.ArrayList;
import java.util.List;

public class OverUtil {
    private long id;
    private int number;
    private List<BallUtil> balls;

    public OverUtil(long id) {
        this.id = id;
        this.number = number;
        this.balls = new ArrayList<>();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public List<BallUtil> getBalls() { return balls; }
    public void setBalls(List<BallUtil> balls) { this.balls = balls; }
}
