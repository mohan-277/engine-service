package com.mohan.gameengineservice.utilities;

public class BallOutcomeUtil {
    private final int runs;        // Runs scored on the ball
    private final boolean isWicket; // Indicates if the ball resulted in a wicket
    private final WicketTypeUtil wicketTypeUtil; // Type of wicket (e.g., CAUGHT_ON_BOWLED, CATCH_OUT)
    private final PlayerObject bowler; // The bowler who delivered the ball
    private final boolean isNoBall;
    private final BallTypeUtil ballTypeUtil;
    // Constructor
    public BallOutcomeUtil(int runs, boolean isWicket, WicketTypeUtil wicketTypeUtil, PlayerObject bowler, BallTypeUtil ballTypeUtil) {
        this.runs = runs;
        this.isWicket = isWicket;
        this.wicketTypeUtil = wicketTypeUtil;
        this.bowler = bowler;
        this.ballTypeUtil = ballTypeUtil;
        this.isNoBall = (ballTypeUtil == BallTypeUtil.NO_BALL);
    }

    public int getRuns() {
        return runs;
    }

    public boolean isWicket() {
        return isWicket;
    }

    public WicketTypeUtil getWicketType() {
        return wicketTypeUtil;
    }

    public PlayerObject getBowler() {
        return bowler;
    }

    public BallTypeUtil getBallType() {
        return ballTypeUtil;
    }

    public boolean isNoBall() {
        return isNoBall;
    }
}
