package com.mohan.gameengineservice.utilities;

public class BallUtil {
    private long id;
    private BallTypeUtil ballTypeUtil;
    private double ballSpeed;
    private PlayerObject playedBy;
    private PlayerObject bowledBy;
    private int run;
    private WicketUtil wicketUtil;

    public BallUtil(long id, BallTypeUtil ballTypeUtil, double ballSpeed, PlayerObject playedBy, PlayerObject bowledBy, int run, WicketUtil wicketUtil) {
        this.id = id;
        this.ballTypeUtil = ballTypeUtil;
        this.ballSpeed = ballSpeed;
        this.playedBy = playedBy;
        this.bowledBy = bowledBy;
        this.run = run;
        this.wicketUtil = wicketUtil;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public BallTypeUtil getBallType() { return ballTypeUtil; }
    public void setBallType(BallTypeUtil ballTypeUtil) { this.ballTypeUtil = ballTypeUtil; }
    public double getBallSpeed() { return ballSpeed; }
    public void setBallSpeed(double ballSpeed) { this.ballSpeed = ballSpeed; }
    public PlayerObject getPlayedBy() { return playedBy; }
    public void setPlayedBy(PlayerObject playedBy) { this.playedBy = playedBy; }
    public PlayerObject getBowledBy() { return bowledBy; }
    public void setBowledBy(PlayerObject bowledBy) { this.bowledBy = bowledBy; }
    public int getRun() { return run; }
    public void setRun(int run) { this.run = run; }
    public WicketUtil getWicket() { return wicketUtil; }
    public void setWicket(WicketUtil wicketUtil) { this.wicketUtil = wicketUtil; }

}
