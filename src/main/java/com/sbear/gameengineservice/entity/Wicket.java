package com.sbear.gameengineservice.entity;

import com.sbear.gameengineservice.entity.constants.WicketType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Wicket {

    @Id
    private  Long id;

    private  WicketType type;
    @OneToOne
    private  PlayerObject outPlayer;
    @OneToOne// this the name of the player who is playing
    private  PlayerObject takenPlayer; // remain all this how is made  him out and what type of wicket it is
    @OneToOne
    private  PlayerObject catchBy;
    @OneToOne
    private  PlayerObject runOutBy;

    public Wicket(PlayerObject playerObject) {
        this.outPlayer = playerObject;
    }

    public Wicket(WicketType wicketType) {
        this.type = wicketType;
    }

    public WicketType getWicketType() {
        return type;
    }
}
