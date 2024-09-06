package com.mohan.gameengineservice.service.impl;


import com.mohan.gameengineservice.entity.*;
import com.mohan.gameengineservice.entity.constants.BallType;
import com.mohan.gameengineservice.repository.*;
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
        innings.setCompleted(false);

        List<PlayerObject> battingPlayers = createPlayerObjects(battingTeam, "Batsman");
        List<PlayerObject> bowlingPlayers = createPlayerObjects(bowlingTeam, "Bowler");

        innings.setCurrentScore("0");
        innings.setCurrentWickets("0");
        innings.setCurrentOvers("0");

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
        int totalOvers = getTotalOvers(innings.getMatch().getMatchType());
        List<PlayerObject> bowlers = getPlayerObjects(innings.getBowlingTeam(), "Bowler");
        List<PlayerObject> battingOrder = getPlayerObjects(innings.getBattingTeam(), "Batsman");

        // Initialize current players
        PlayerObject currentBowler = bowlers.get(0);
        PlayerObject striker = battingOrder.get(0);
        PlayerObject nonStriker = battingOrder.get(1);

        innings.setCurrentBowler(currentBowler);
        innings.setCurrentStriker(striker);
        innings.setCurrentNonStriker(nonStriker);

        for (int over = 1; over <= totalOvers; over++) {
            Over overEntity = createOver(innings, over);
            for (int ball = 1; ball <= 6; ball++) {
                if (innings.getWickets() >= 10 || battingOrder.size() < 2) {
                    break; // End innings if 10 wickets are down or no more batters
                }
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
            innings.setCurrentBowler(currentBowler);
        }

        innings.setCompleted(true);
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
        ball.setBallSpeed(random.nextDouble() * 10);
        ball.setRun(runs);
        ball.setPlayedBy(striker); // Use PlayerObject directly
        ball.setBowledBy(bowler); // Use PlayerObject directly

        ballRepository.save(ball);

        // Update innings score based on the event
        updateInningsScore(innings, runs, isWicket, striker, nonStriker, bowler, battingOrder, ball);
    }



    private void updatePlayerStats(PlayerObject playerObject, int runs, boolean isWicket) {
        // Update runs, balls faced, etc.
        playerObject.setScore(playerObject.getScore() + runs);
        playerObject.setBallsFaced(playerObject.getBallsFaced() + 1);

        // Increment fours and sixes if applicable
        if (runs == 4) {
            playerObject.setFours(playerObject.getFours() + 1);
        } else if (runs == 6) {
            playerObject.setSixes(playerObject.getSixes() + 1);
        }

        // Check if out and update the player's status
        if (isWicket) {
            playerObject.getPlayer().setOut(true);
        }

        // Save player stats to the repository
        playerObjectRepository.save(playerObject);
    }



    private void updateInningsScore(Innings innings, int runs, boolean isWicket, PlayerObject striker, PlayerObject nonStriker, PlayerObject bowler, List<PlayerObject> battingOrder, Ball ball) {
        // Update innings score
        innings.setRuns(innings.getRuns() + runs);
        innings.setCurrentScore(String.valueOf(innings.getRuns()));
        if (isWicket) {
            innings.setWickets(innings.getWickets() + 1);
            innings.setCurrentWickets(String.valueOf(innings.getWickets()));

            // Update the striker's score and status
            striker.setScore(striker.getScore() + runs);
            striker.setBallsFaced(striker.getBallsFaced() + 1);
            striker.getPlayer().setOut(true);

            // Replace the out player with a new batsman from the bench
            PlayerObject newStriker = getNewBatsman(innings.getBattingTeam(), battingOrder);
            innings.setCurrentStriker(newStriker); // New batsman replaces striker

            // Update the player object list to reflect the new striker
            battingOrder.remove(striker);
            battingOrder.add(newStriker);

        } else {
            // Update the striker's score and statistics
            striker.setScore(striker.getScore() + runs);
            striker.setBallsFaced(striker.getBallsFaced() + 1);

            if (runs == 4) {
                striker.setFours(striker.getFours() + 1);
            } else if (runs == 6) {
                striker.setSixes(striker.getSixes() + 1);
            }
        }

        // Update PlayerObject stats with ball information
        striker.getBalls().add(ball);
        bowler.getBalls().add(ball);

        // Save updated PlayerObject stats
        playerObjectRepository.save(striker);
        playerObjectRepository.save(bowler);

        // Save updated innings
        inningsRepository.save(innings);
    }

    private PlayerObject getNewBatsman(Team battingTeam, List<PlayerObject> battingOrder) {
        // Logic to select a new batsman from the bench or remaining players
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
        int scoreA = inningsA.getRuns();
        int scoreB = inningsB.getRuns();
        int wicketsA = inningsA.getWickets();
        int wicketsB = inningsB.getWickets();

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
        // Update the team's statistics based on the innings
        // This could include runs scored, wickets taken, etc.
        // For example, increment the team's total runs and wickets
        team.setTotalPoints(team.getTotalPoints() + calculatePoints(innings)); // Method to calculate points based on performance
        teamRepository.save(team);
    }

    private int calculatePoints(Innings innings) {
        // Implement logic to calculate points based on innings performance
        // This is an example; adjust as per your scoring system
        int points = 0;
        int runs = innings.getRuns();
        int wickets = innings.getWickets();

        if (runs > 200) {
            points += 2; // Example points for a high score
        }
        if (wickets < 5) {
            points += 1; // Example points for a low number of wickets lost
        }
        return points;
    }

}


/*
* this is for the only complete scoring functionality and innings related  tomorrow i need to solve this errors and check the test
* */