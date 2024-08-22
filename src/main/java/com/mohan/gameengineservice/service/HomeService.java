package com.mohan.gameengineservice.service;


import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Team;

import java.util.List;

public interface HomeService {
    // endPoint it shows all the available match mix of all
    List<CricketMatch> matches();
    List<Team> teams();

}
