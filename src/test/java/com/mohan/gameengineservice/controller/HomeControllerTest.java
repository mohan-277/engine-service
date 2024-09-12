package com.mohan.gameengineservice.controller;


import com.mohan.gameengineservice.controller.HomeController;
import com.mohan.gameengineservice.dto.*;
import com.mohan.gameengineservice.entity.constants.MatchStage;
import com.mohan.gameengineservice.entity.*;
import com.mohan.gameengineservice.repository.*;
import com.mohan.gameengineservice.service.impl.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class HomeControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private CricketMatchRepository cricketMatchRepository;

    @MockBean
    private PlayerRepository playerRepository;

    @MockBean
    private TeamRepository teamRepository;

    @MockBean
    private TeamService teamService;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    public void setup() {
        Team teamA = new Team();
        teamA = new Team();
        teamA.setTeamId(1L);
        teamA.setName("Team A");

        Team teamB = new Team();
        teamB = new Team();
        teamB.setTeamId(2L);
        teamB.setName("Team B");

        // Mock data for Location
        Location location = new Location();
        location = new Location();
        location.setId(1L);
        location.setCountry("India");

        // Mock data for Tournament

        Tournament tournament = new Tournament();
        tournament = new Tournament();
        tournament.setId(1L);
        tournament.setTournamentName("IPL");

        // Mock data for CricketMatch
        CricketMatch cricketMatch = new CricketMatch();
        cricketMatch = new CricketMatch();
        cricketMatch.setId(1L);
        cricketMatch.setStadiumName("Wankhede Stadium");
        cricketMatch.setToss(true);
        cricketMatch.setTossDecision("batting");
        cricketMatch.setTeamA(teamA);
        cricketMatch.setTeamB(teamB);
        cricketMatch.setMatchDateTime(LocalDateTime.now());
        cricketMatch.setLocation(location);
        cricketMatch.setMatchType("T20");
        cricketMatch.setMatchStage(MatchStage.GROUP_STAGE); // assuming MatchStage is an enum
        cricketMatch.setMatchGroup("Group A");
        cricketMatch.setTournament(tournament);
        cricketMatch.setLive(true);
        cricketMatch.setResult("Team A won by 5 runs");
    }
    }


