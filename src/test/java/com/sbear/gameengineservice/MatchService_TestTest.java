package com.sbear.gameengineservice;

import com.sbear.gameengineservice.entity.CricketMatch;
import com.sbear.gameengineservice.entity.Innings;
import com.sbear.gameengineservice.entity.Team;
import com.sbear.gameengineservice.repository.CricketMatchRepository;
import com.sbear.gameengineservice.repository.InningsRepository;
import com.sbear.gameengineservice.service.impl.MatchService;
import com.sbear.gameengineservice.service.impl.MatchService_Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


public class MatchService_TestTest {

    @InjectMocks
    private MatchService matchService;

    @InjectMocks
    private MatchService_Test matchServiceTest;

    @Mock
    private InningsRepository inningsRepository;

    @Mock
    private CricketMatchRepository matchRepository;

    private CricketMatch match;
    private Team teamA;
    private Team teamB;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        teamA = new Team();
        teamA.setName("Team A");

        teamB = new Team();
        teamB.setName("Team B");

        match = new CricketMatch();
        match.setTeamA(teamA);
        match.setTeamB(teamB);
    }


}
