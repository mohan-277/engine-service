package com.sbear.gameengineservice.service.impl;

import com.sbear.gameengineservice.entity.*;
import com.sbear.gameengineservice.entity.constants.BallType;
import com.sbear.gameengineservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class MatchService {

    @Autowired
    private CricketMatchRepository matchRepository;

    @Autowired
    private InningsRepository inningsRepository;

    @Autowired
    private OverRepository overRepository;

    @Autowired
    private BallRepository ballRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerObjectRepository playerObjectRepository;

    public void startMatch(Long matchId) {
        CricketMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (!match.isLive()) {
            simulateMatch(match);
            match.setLive(true);
            matchRepository.save(match);
        }
    }

    private void simulateMatch(CricketMatch match) {
        Innings inningsA = createInnings(match, match.getTeamA(), match.getTeamB());
        Innings inningsB = createInnings(match, match.getTeamB(), match.getTeamA());

        simulateInnings(inningsA);
        simulateInnings(inningsB);

        // Update the match results after both innings
        updateMatchResults(match, inningsA, inningsB);
    }

    private Innings createInnings(CricketMatch match, Team battingTeam, Team bowlingTeam) {
        Innings innings = new Innings();
        innings.setMatch(match);
        innings.setBattingTeam(battingTeam);
        innings.setBowlingTeam(bowlingTeam);
        innings.setIsCompleted(false);

        List<PlayerObject> battingPlayers = createPlayerObjects(battingTeam, "Batsman");
        List<PlayerObject> bowlingPlayers = createPlayerObjects(bowlingTeam, "Bowler");

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

        // Initialize current players
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
//                    break; // End innings if 10 wickets are down or no more batters
//                }
                simulateBallEvent(innings, overEntity, ball, currentBowler, striker, nonStriker, battingOrder);

                // Swap striker and non-striker if needed
                if (ball % 6 == 0) {
                    PlayerObject temp = striker;
                    striker = nonStriker;
                    nonStriker = temp;
                }
            }

            // Rotate bowlers
            int bowlerIndex = (bowlers.indexOf(currentBowler) + 1) % bowlers.size();
            currentBowler = bowlers.get(bowlerIndex);
//            innings.setCurrentBowler(currentBowler);
        }

        innings.setIsCompleted(true);
        inningsRepository.save(innings);
    }

    private Over createOver(Innings innings, int overNumber) {
        Over over = new Over();
        over.setNumbers(overNumber);
        over.setInnings(innings);
        overRepository.save(over);
        return over;
    }

//    private void simulateBallEvent(Innings innings, Over over, int ballNumber, PlayerObject bowler, PlayerObject striker, PlayerObject nonStriker, List<PlayerObject> battingOrder) {
//        Random random = new Random();
//        int runs = random.nextInt(7); // Runs between 0 and 6
//        boolean isWicket = random.nextBoolean(); // Randomly decide if it's a wicket
//
//        Ball ball = new Ball();
//        ball.setBallType(isWicket ? BallType.WICKET : BallType.RUN);
//        ball.setBallSpeed(random.nextDouble() * 10);
//        ball.setRun(runs);
//        ball.setPlayedBy(striker.getPlayer());
//        ball.setBowledBy(bowler.getPlayer());
//
//        ballRepository.save(ball);
//
//        updateInningsScore(innings, runs, isWicket, striker, nonStriker, bowler, battingOrder);
//    }

//    private void simulateBallEvent(Innings innings, Over over, int ballNumber, PlayerObject bowler, PlayerObject striker, PlayerObject nonStriker, List<PlayerObject> battingOrder) {
//        Random random = new Random();
//        int runs = random.nextInt(7); // Runs between 0 and 6
//        boolean isWicket = random.nextBoolean(); // Randomly decide if it's a wicket
//
//        Ball ball = new Ball();
//        ball.setBallType(isWicket ? BallType.WICKET : BallType.RUN);
//        ball.setBallSpeed(random.nextDouble() * 10);
//        ball.setRun(runs);
//        ball.setPlayedBy(striker); // Use PlayerObject directly
//        ball.setBowledBy(bowler); // Use PlayerObject directly
//
//        ballRepository.save(ball);
//
//        // Update innings score based on the event
//        updateInningsScore(innings, runs, isWicket, striker, nonStriker, bowler, battingOrder);
//
//        // Update player stats
//        updatePlayerStats(striker, runs, isWicket);
//    }

    private void simulateBallEvent(Innings innings, Over over, int ballNumber, PlayerObject bowler, PlayerObject striker, PlayerObject nonStriker, List<PlayerObject> battingOrder) {
        Random random = new Random();
        int runs = random.nextInt(7); // Runs between 0 and 6
        boolean isWicket = random.nextBoolean(); // Randomly decide if it's a wicket

        Ball ball = new Ball();
        ball.setBallType(isWicket ? BallType.WICKET : BallType.RUN);
//        ball.setBallSpeed(random.nextDouble() * 10);
//        ball.setRun(runs);
//        ball.setPlayedBy(striker);
//        ball.setBowledBy(bowler);

        ballRepository.save(ball);

        // Update innings score based on the event
        updateInningsScore(innings, runs, isWicket, striker, nonStriker, bowler, battingOrder, ball);
    }



    private void updatePlayerStats(PlayerObject playerObject, int runs, boolean isWicket) {

        playerObject.setScore(playerObject.getScore() + runs);
        playerObject.setBallsFaced(playerObject.getBallsFaced() + 1);

        // Increment fours and sixes if applicable
        if (runs == 4) {
            playerObject.setFours(playerObject.getFours() + 1);
        } else if (runs == 6) {
            playerObject.setSixes(playerObject.getSixes() + 1);
        }

        if (isWicket) {
            playerObject.getPlayer().setOut(true);
        }

        // Save player stats to the repository
        playerObjectRepository.save(playerObject);
    }



    private void updateInningsScore(Innings innings, int runs, boolean isWicket, PlayerObject striker, PlayerObject nonStriker, PlayerObject bowler, List<PlayerObject> battingOrder, Ball ball) {
        // Update innings score
//        innings.setRuns(innings.getRuns() + runs);
//        innings.setCurrentScore(String.valueOf(innings.getRuns()));
//        if (isWicket) {
//            innings.setWickets(innings.getWickets() + 1);
//            innings.setCurrentWickets(String.valueOf(innings.getWickets()));
//
//            // Update the striker's score and status
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
//            // Update the striker's score and statistics
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
//        // Update PlayerObject stats with ball information
////        striker.getBalls().add((Runnable) ball);
////        bowler.getBalls().add((Runnable) ball);
//
//        // Save updated PlayerObject stats
//        playerObjectRepository.save(striker);
//        playerObjectRepository.save(bowler);

        // Save updated innings
        inningsRepository.save(innings);
    }

    private PlayerObject getNewBatsman(Team battingTeam, List<PlayerObject> battingOrder) {
        // Logic to get the Object for the batting order
        // Ensure the new batsman isn't already playing
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
                        playerObject.setPlayer(player); // This should be valid
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
        switch (matchType) {
            case "T20":
                return 20;
            case "ODI":
                return 50;
            case "Test":
                return 90;
            default:
                throw new IllegalArgumentException("Unknown match type: " + matchType);
        }
    }



    private void updateMatchResults(CricketMatch match, Innings inningsA, Innings inningsB) {
        // Retrieve the scores and wickets for both innings
//        int scoreA = inningsA.getRuns();
//        int scoreB = inningsB.getRuns();
//        int wicketsA = inningsA.getWickets();
//        int wicketsB = inningsB.getWickets();

        int scoreA = 0;
        int scoreB =   0;
        int wicketsA = 0;
        int wicketsB = 0;

        // Determine the winner
        String result;
        if (scoreA > scoreB) {
            result = match.getTeamA().getName() + " won by " + (scoreA - scoreB) + " runs";
        } else if (scoreB > scoreA) {
            result = match.getTeamB().getName() + " won by " + (10 - wicketsB) + " wickets";
        } else {
            result = "Match tied";
        }

        // Update match result in the match entity
        match.setResult(result);
        match.setLive(false); // Mark the match as not live
        matchRepository.save(match);

        // Optionally update team statistics here
        updateTeamStatistics(match.getTeamA(), inningsA);
        updateTeamStatistics(match.getTeamB(), inningsB);
    }

    private void updateTeamStatistics(Team team, Innings innings) {

        team.setTotalPoints(team.getTotalPoints() + calculatePoints(innings)); // Method to calculate points based on performance
        teamRepository.save(team);
    }

    private int calculatePoints(Innings innings) {

        int points = 0;
//        int runs = innings.getRuns();
//        int wickets = innings.getWickets();

//        if (runs > 200) {
//            points += 2;
//        }
//        if (wickets < 5) {
//            points += 1;
//        }
        return points;
    }


}


/*
* this is for the only complete scoring functionality and innings related  tomorrow i need to solve this errors and check the test
* */