package com.mohan.gameengineservice.service;


import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Player;
import com.mohan.gameengineservice.entity.Team;

import java.util.List;

public interface HomeService {
    // endPoint it shows all the available match mix of all
    public List<CricketMatch> matches();
    public List<Team> teams();
    public List<Player> players();

}
