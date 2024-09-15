package com.sbear.gameengineservice.service.impl;

import com.sbear.gameengineservice.dto.MatchDto;

import com.sbear.gameengineservice.entity.*;
import com.sbear.gameengineservice.entity.constants.BallType;
import com.sbear.gameengineservice.repository.*;
import com.sbear.gameengineservice.websocket.services.MatchWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class MatchService_Test {

    @Autowired
    private CricketMatchRepository matchRepository;

    @Autowired
    private InningsRepository inningsRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerObjectRepository playerObjectRepository;

    @Autowired
    private BallRepository ballRepository;

    @Autowired
    private OverRepository overRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchWebSocketHandler webSocketHandler;



    public void startMatch(Long matchId) {
        CricketMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (!match.isLive()) {
            simulateMatch(match);
            match.setLive(true);
            matchRepository.save(match);
            // Send the initial match update
            webSocketHandler.sendMatchUpdate(getMatchStatus(matchId));
        }
    }

    private void simulateMatch(CricketMatch match) {
        Innings inningsA = createInnings(match, match.getTeamA(), match.getTeamB());
        Innings inningsB = createInnings(match, match.getTeamB(), match.getTeamA());

        simulateInnings(inningsA);
        simulateInnings(inningsB);

        updateMatchResults(match, inningsA, inningsB);
        // Send updates after match results are updated
        webSocketHandler.sendMatchUpdate(getMatchStatus(match.getId()));
    }

    private Innings createInnings(CricketMatch match, Team battingTeam, Team bowlingTeam) {
        Innings innings = new Innings();
        innings.setMatch(match);
        innings.setBattingTeam(battingTeam);
        innings.setBowlingTeam(bowlingTeam);
        innings.setIsCompleted(false);

        List<PlayerObject> battingPlayers = createPlayerObjects(battingTeam, "Batsman");
        List<PlayerObject> bowlingPlayers = createPlayerObjects(bowlingTeam, "Bowler");
//
//        innings.setCurrentScore("0");
//        innings.setCurrentWickets("0");
//        innings.setCurrentOvers("0");

        inningsRepository.save(innings);
        return innings;
    }

    private List<PlayerObject> createPlayerObjects(Team team, String specialization) {
        List<Player> players = playerRepository.findByTeamAndSpecialization(team, specialization);
        return players.stream()
                .map(player -> {
                    PlayerObject playerObject = new PlayerObject();
                    playerObject.setPlayer(player);
                    playerObject.setScore(0);
                    playerObject.setBallsFaced(0);
                    playerObject.setFours(0);
                    playerObject.setSixes(0);
                    return playerObject;
                })
                .collect(Collectors.toList());
    }

    private void simulateInnings(Innings innings) {
        int totalOvers = getTotalOvers(String.valueOf(innings.getMatch().getMatchType()));
        List<PlayerObject> bowlers = getPlayerObjects(innings.getBowlingTeam(), "Bowler");
        List<PlayerObject> battingOrder = getPlayerObjects(innings.getBattingTeam(), "Batsman");

        PlayerObject currentBowler = bowlers.get(0);
        PlayerObject striker = battingOrder.get(0);
        PlayerObject nonStriker = battingOrder.get(1);

//        innings.setCurrentBowler(currentBowler);
//        innings.setCurrentStriker(striker);
//        innings.setCurrentNonStriker(nonStriker);

        for (int over = 1; over <= totalOvers; over++) {
            Over overEntity = createOver(innings, over);
            for (int ball = 1; ball <= 6; ball++) {
//                if (innings.getWickets() >= 10 || battingOrder.size() < 2) {
//                    break;
//                }
                simulateBallEvent(innings, overEntity, ball, currentBowler, striker, nonStriker, battingOrder);

                if (ball % 6 == 0) {
                    PlayerObject temp = striker;
                    striker = nonStriker;
                    nonStriker = temp;
                }
            }

            int bowlerIndex = (bowlers.indexOf(currentBowler) + 1) % bowlers.size();
            currentBowler = bowlers.get(bowlerIndex);
//            innings.setCurrentBowler(currentBowler);
        }

//        innings.setCompleted(true);
        inningsRepository.save(innings);
        // Send update after innings completion
        webSocketHandler.sendMatchUpdate(getMatchStatus(innings.getMatch().getId()));
    }

    private Over createOver(Innings innings, int overNumber) {
        Over over = new Over();
        over.setNumbers(overNumber);
        over.setInnings(innings);
        overRepository.save(over);
        return over;
    }

    private void simulateBallEvent(Innings innings, Over over, int ballNumber, PlayerObject bowler, PlayerObject striker, PlayerObject nonStriker, List<PlayerObject> battingOrder) {
        Random random = new Random();
        int runs = random.nextInt(7);
        boolean isWicket = random.nextBoolean();

        Ball ball = new Ball();
        ball.setBallType(isWicket ? BallType.WICKET : BallType.RUN);
//        ball.setBallSpeed(random.nextDouble() * 10);
//        ball.setRun(runs);
//        ball.setPlayedBy(striker);
//        ball.setBowledBy(bowler);

        ballRepository.save(ball);

        updateInningsScore(innings, runs, isWicket, striker, nonStriker, bowler, battingOrder, ball);
    }

    private void updatePlayerStats(PlayerObject playerObject, int runs, boolean isWicket) {
        playerObject.setScore(playerObject.getScore() + runs);
        playerObject.setBallsFaced(playerObject.getBallsFaced() + 1);

        if (runs == 4) {
            playerObject.setFours(playerObject.getFours() + 1);
        } else if (runs == 6) {
            playerObject.setSixes(playerObject.getSixes() + 1);
        }

        if (isWicket) {
            playerObject.getPlayer().setOut(true);
        }

        playerObjectRepository.save(playerObject);
    }

    private void updateInningsScore(Innings innings, int runs, boolean isWicket, PlayerObject striker, PlayerObject nonStriker, PlayerObject bowler, List<PlayerObject> battingOrder, Ball ball) {
//        innings.setRuns(innings.getRuns() + runs);
//        innings.setCurrentScore(String.valueOf(innings.getRuns()));
//        if (isWicket) {
//            innings.setWickets(innings.getWickets() + 1);
//            innings.setCurrentWickets(String.valueOf(innings.getWickets()));
//
//            striker.setScore(striker.getScore() + runs);
//            striker.setBallsFaced(striker.getBallsFaced() + 1);
//            striker.getPlayer().setOut(true);
//
//            PlayerObject newStriker = getNewBatsman(innings.getBattingTeam(), battingOrder);
//            innings.setCurrentStriker(newStriker);
//
//            battingOrder.remove(striker);
//            battingOrder.add(newStriker);
//
//        } else {
//            striker.setScore(striker.getScore() + runs);
//            striker.setBallsFaced(striker.getBallsFaced() + 1);
//
//            if (runs == 4) {
//                striker.setFours(striker.getFours() + 1);
//            } else if (runs == 6) {
//                striker.setSixes(striker.getSixes() + 1);
//            }
//        }
//
//        playerObjectRepository.save(striker);
//        playerObjectRepository.save(bowler);
//
//        inningsRepository.save(innings);
//        // Send update after scoring
        webSocketHandler.sendMatchUpdate(getMatchStatus(innings.getMatch().getId()));
    }

    private PlayerObject getNewBatsman(Team battingTeam, List<PlayerObject> battingOrder) {
        return battingOrder.stream()
                .filter(player -> !player.getPlayer().isOut())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available batsman"));
    }

    private List<PlayerObject> getPlayerObjects(Team team, String specialization) {
        return playerRepository.findByTeamAndSpecialization(team, specialization).stream()
                .map(player -> {
                    PlayerObject playerObject = playerObjectRepository.findByPlayer(player);
                    if (playerObject == null) {
                        playerObject = new PlayerObject();
                        playerObject.setPlayer(player);
                        playerObject.setScore(0);
                        playerObject.setBallsFaced(0);
                        playerObject.setFours(0);
                        playerObject.setSixes(0);
                    }
                    return playerObject;
                })
                .collect(Collectors.toList());
    }

    private int getTotalOvers(String matchType) {
        return switch (matchType) {
            case "IPL" -> 10;
            case "T20" -> 20;
            case "ODI" -> 50;
            case "Test" -> 90;
            default -> throw new IllegalArgumentException("Unknown match type: " + matchType);
        };
    }

    private void updateMatchResults(CricketMatch match, Innings inningsA, Innings inningsB) {
//        int scoreA = inningsA.getRuns();
//        int scoreB = inningsB.getRuns();
//        int wicketsA = inningsA.getWickets();
//        int wicketsB = inningsB.getWickets();
        int scoreA = 0;
        int scoreB =   0;
        int wicketsA = 0;
        int wicketsB = 0;

        String result;
        if (scoreA > scoreB) {
            result = match.getTeamA().getName() + " won by " + (scoreA - scoreB) + " runs";
        } else if (scoreB > scoreA) {
            result = match.getTeamB().getName() + " won by " + (10 - wicketsB) + " wickets";
        } else {
            result = "Match drawn";
        }

        match.setResult(result);
        matchRepository.save(match);
        // Send update after match results
        webSocketHandler.sendMatchUpdate(getMatchStatus(match.getId()));
    }

    public String getMatchStatus(Long matchId) {
        CricketMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        MatchDto dto = new MatchDto();
        dto.setId(match.getId());
        dto.setTeamA(match.getTeamA().getName());
        dto.setTeamB(match.getTeamB().getName());
        dto.setCurrentScore(getCurrentScore(match));
        dto.setResult(match.getResult());
        dto.setLive(match.isLive());
        return dto+"";
    }

    public String getCurrentScore(CricketMatch match) {
        List<Innings> inningsList = inningsRepository.findInningsByMatchId(match.getId());

        // Map to store innings by team
        Map<Team, Innings> inningsMap = new HashMap<>();

        for (Innings innings : inningsList) {
            inningsMap.put(innings.getBattingTeam(), innings);
        }

        Team teamA = match.getTeamA();
        Team teamB = match.getTeamB();

        Innings inningsA = inningsMap.get(teamA);
        Innings inningsB = inningsMap.get(teamB);

        // Build the score string
//        String scoreA = (inningsA != null) ? inningsA.getRuns() + "/" + inningsA.getWickets() : "0/0";
//        String scoreB = (inningsB != null) ? inningsB.getRuns() + "/" + inningsB.getWickets() : "0/0";
        String scoreA = "";
        String scoreB = "";


        return scoreA + " - " + scoreB;
    }
}
