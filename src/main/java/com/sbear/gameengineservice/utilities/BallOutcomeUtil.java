package com.sbear.gameengineservice.utilities;

import lombok.Getter;

public class BallOutcomeUtil {
    @Getter
    private final Integer runs;        // Runs scored on the ball
    private final boolean isWicket; // Indicates if the ball resulted in a wicket
    private final WicketTypeUtil wicketTypeUtil; // Type of wicket (e.g., CAUGHT_ON_BOWLED, CATCH_OUT)
    @Getter
    private final PlayerObject bowler; // The bowler who delivered the ball
    private final boolean isNoBall;
    private final BallTypeUtil ballTypeUtil;


    public BallOutcomeUtil(int runs, boolean isWicket, WicketTypeUtil wicketTypeUtil, PlayerObject bowler, BallTypeUtil ballTypeUtil) {
        this.runs = runs;
        this.isWicket = isWicket;
        this.wicketTypeUtil = wicketTypeUtil;
        this.bowler = bowler;
        this.ballTypeUtil = ballTypeUtil;
        this.isNoBall = (ballTypeUtil == BallTypeUtil.NO_BALL);
    }

    public boolean isWicket() {
        return isWicket;
    }

    public WicketTypeUtil getWicketType() {
        return wicketTypeUtil;
    }

    public BallTypeUtil getBallType() {
        return ballTypeUtil;
    }

    public boolean isNoBall() {
        return isNoBall;
    }
}
