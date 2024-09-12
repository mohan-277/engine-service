package com.mohan.gameengineservice.utilities;

public class WicketUtil {

    private  Long id;

    private WicketTypeUtil type;

    private  PlayerObject outPlayer;

    private  PlayerObject takenPlayer; // remain all this how is made  him out and what type of wicket it is

    private  PlayerObject catchBy;

    private  PlayerObject runOutBy;

    public WicketUtil(Long id, WicketTypeUtil type, PlayerObject outPlayer, PlayerObject takenPlayer,
                      PlayerObject catchBy, PlayerObject runOutBy) {
        this.id = id;
        this.type = type;
        this.outPlayer = outPlayer;
        this.takenPlayer = takenPlayer;
        this.catchBy = catchBy;
        this.runOutBy = runOutBy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public WicketTypeUtil getType() { return type; }
    public void setType(WicketTypeUtil type) { this.type = type; }
    public PlayerObject getOutPlayer() { return outPlayer; }
    public void setOutPlayer(PlayerObject outPlayer) { this.outPlayer = outPlayer; }
    public PlayerObject getTakenPlayer() { return takenPlayer; }
    public void setTakenPlayer(PlayerObject takenPlayer) { this.takenPlayer = takenPlayer; }
    public PlayerObject getCatchBy() { return catchBy; }
    public void setCatchBy(PlayerObject catchBy) { this.catchBy = catchBy; }
    public PlayerObject getRunOutBy() { return runOutBy; }
    public void setRunOutBy(PlayerObject runOutBy) { this.runOutBy = runOutBy; }

}
