package com.mohan.gameengineservice;

import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Innings;
import com.mohan.gameengineservice.entity.Team;
import com.mohan.gameengineservice.repository.CricketMatchRepository;
import com.mohan.gameengineservice.repository.InningsRepository;
import com.mohan.gameengineservice.service.impl.MatchService;
import com.mohan.gameengineservice.service.impl.MatchService_Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Service;

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

    @Test
    public void testGetCurrentScore() {
        // Create innings
        Innings inningsA = new Innings();
        inningsA.setBattingTeam(teamA);
        inningsA.setRuns(150);
        inningsA.setWickets(4);

        Innings inningsB = new Innings();
        inningsB.setBattingTeam(teamB);
        inningsB.setRuns(120);
        inningsB.setWickets(6);

        // Mock repository method
        List<Innings> inningsList = new ArrayList<>();
        inningsList.add(inningsA);
        inningsList.add(inningsB);
        when(inningsRepository.findInningsByMatchId(match.getId())).thenReturn(inningsList);

        // Call the method and assert results
        String score = matchServiceTest.getCurrentScore(match);
        assertEquals("150/4 - 120/6", score);
    }
}
