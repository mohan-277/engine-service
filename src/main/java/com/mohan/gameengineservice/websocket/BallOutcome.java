package com.mohan.gameengineservice.websocket;

import com.mohan.gameengineservice.entity.PlayerObject;
import com.mohan.gameengineservice.entity.constants.BallType;
import com.mohan.gameengineservice.entity.constants.WicketType;
import lombok.Getter;

public class BallOutcome {
    private final int runs;        // Runs scored on the ball
    private final boolean isWicket; // Indicates if the ball resulted in a wicket

    private final WicketType wicketType; // Type of wicket (e.g., CAUGHT_ON_BOWLED, CATCH_OUT)
    private final PlayerObject bowler; // The bowler who delivered the ball
    private final boolean isNoBall;
    private final BallType ballType;

    // Constructor
    public BallOutcome(int runs, boolean isWicket, WicketType wicketType, PlayerObject bowler, BallType ballType) {
        this.runs = runs;
        this.isWicket = isWicket;
        this.wicketType = wicketType;
        this.bowler = bowler;
        this.ballType = ballType;
        this.isNoBall = (ballType == BallType.NO_BALL);
    }

    public BallOutcome(int runs, boolean isWicket, BallType ballType, PlayerObject bowler, boolean isExtra, WicketType wicketType) {
        this.runs = runs;
        this.isWicket = isWicket;
        this.ballType = ballType;
        this.bowler = bowler;
        this.isNoBall = isExtra;
        this.wicketType = wicketType; // Initialize WicketType
    }

    public int getRuns() {
        return runs;
    }

    public boolean isWicket() {
        return isWicket;
    }

    public PlayerObject getBowler() {
        return bowler;
    }

    public BallType getBallType() {
        return ballType;
    }

    public boolean isNoBall() {
        return isNoBall;
    }

    public String getWicketType() {
        return wicketType.toString();
    }
}
