package com.mohan.gameengineservice.entity;

import com.mohan.gameengineservice.entity.constants.WicketType;

public class Wicket {
    WicketType type;
    PlayerObject outPlayer; // this the name of the player who is playing
    PlayerObject takenPlayer; // remain all this how is made  him out and what type of wicket it is
    PlayerObject catchBy;
    PlayerObject runOutBy;
}
