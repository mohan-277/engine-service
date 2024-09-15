package com.sbear.gameengineservice.utilities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

}
