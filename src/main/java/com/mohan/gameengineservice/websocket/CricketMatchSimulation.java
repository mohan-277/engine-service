package com.mohan.gameengineservice.websocket;

import com.mohan.gameengineservice.dto.MatchDetailsDTO;
import com.mohan.gameengineservice.entity.*;
import com.mohan.gameengineservice.entity.constants.BallType;
import com.mohan.gameengineservice.entity.constants.WicketType;
import com.mohan.gameengineservice.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CricketMatchSimulation {


        @Autowired
        private CricketMatchRepository cricketMatchRepository;

        @Autowired
        private InningsRepository inningsRepository;

        @Autowired
        private TeamRepository teamRepository;

        @Autowired
        PlayerRepository playerRepository;

        @Autowired
        BallRepository ballRepository;

    @Transactional
    public void simulateMatchFromDTO(MatchDetailsDTO matchDTO) throws InterruptedException {
        Long matchId = matchDTO.getMatchId();
        System.out.println("Match ID: " + matchId);
        CricketMatch match = cricketMatchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        // Fetch teams from repository
        Team teamA = teamRepository.findByName(matchDTO.getTeamA())
                .orElseThrow(() -> new IllegalArgumentException("Team A not found"));
        Team teamB = teamRepository.findByName(matchDTO.getTeamB())
                .orElseThrow(() -> new IllegalArgumentException("Team B not found"));

        // Check if innings exist, if not, create them based on toss
        List<Innings> inningsList = match.getInnings();
        if (inningsList == null || inningsList.size() < 2) {
            // Perform toss and create innings if not present
            performTossAndCreateInnings(matchDTO);
            inningsList = match.getInnings(); // Refresh innings list
        }

        // Ensure inningsList contains at least two innings
        if (inningsList.size() < 2) {
            throw new IllegalStateException("Innings not properly created. Expected 2 innings but found " + inningsList.size());
        }

        // Fetch innings by ID
        Innings inningsA = inningsRepository.findById(inningsList.get(0).getId())
                .orElseThrow(() -> new IllegalArgumentException("Innings A not found"));

        Innings inningsB = inningsRepository.findById(inningsList.get(1).getId())
                .orElseThrow(() -> new IllegalArgumentException("Innings B not found"));

        // Check if innings have the necessary teams
        if (inningsA.getBattingTeam() == null || inningsA.getBowlingTeam() == null ||
                inningsB.getBattingTeam() == null || inningsB.getBowlingTeam() == null) {
            throw new IllegalArgumentException("Innings teams not properly set");
        }

        // Simulate the innings
        simulateInnings(inningsA);
        simulateInnings(inningsB);
        System.out.println("Team A: " + teamA);
        System.out.println("Team B: " + teamB);

        // Print the match result
//        printMatchResult(inningsA, inningsB);

        // Update match status
        match.setLive(false); // Set to true if marking as live
        cricketMatchRepository.save(match);
    }




    @Transactional
    public void performTossAndCreateInnings(MatchDetailsDTO matchDTO) {
        Long matchId = matchDTO.getMatchId();
        CricketMatch match = cricketMatchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        // Fetch teams from repository
        Team teamA = teamRepository.findByName(matchDTO.getTeamA())
                .orElseThrow(() -> new IllegalArgumentException("Team A not found"));
        Team teamB = teamRepository.findByName(matchDTO.getTeamB())
                .orElseThrow(() -> new IllegalArgumentException("Team B not found"));

        // Initialize innings if not present
        if (match.getInnings().isEmpty()) {
            Innings inningsA = new Innings();
            Innings inningsB = new Innings();

            // Set match reference
            inningsA.setCricketMatch(match);
            inningsB.setCricketMatch(match);

            // Set teams for the innings
            inningsA.setBattingTeam(teamA);
            inningsB.setBattingTeam(teamB);

            // Assume toss decision is done here, so set bowling teams accordingly
            inningsA.setBowlingTeam(teamB); // Set bowling team for Innings A
            inningsB.setBowlingTeam(teamA); // Set bowling team for Innings B

            // Set default values for other properties
            inningsA.setRuns(0);
            inningsA.setWickets(0);
            inningsA.setCurrentScore("0/0");
            inningsA.setCurrentWickets("0");
            inningsA.setCurrentOvers("0");
            inningsA.setCompleted(false);

            inningsB.setRuns(0);
            inningsB.setWickets(0);
            inningsB.setCurrentScore("0/0");
            inningsB.setCurrentWickets("0");
            inningsB.setCurrentOvers("0");
            inningsB.setCompleted(false);

            // Add innings to the match
            match.getInnings().add(inningsA);
            match.getInnings().add(inningsB);

            // Save entities
            cricketMatchRepository.save(match);
            inningsRepository.saveAll(Arrays.asList(inningsA, inningsB));
        }
    }


//    private void simulateInnings(Innings innings) throws InterruptedException {
//            System.out.println("Innings Start: " + innings.getBattingTeam().getName());
//
//            List<Player> battingPlayers = innings.getBattingTeam().getPlayers();
//            List<Player> bowlingPlayers = innings.getBowlingTeam().getPlayers();
//
//            if (battingPlayers == null || battingPlayers.isEmpty()) {
//                throw new IllegalArgumentException("No batting players available");
//            }
//            if (bowlingPlayers == null || bowlingPlayers.isEmpty()) {
//                throw new IllegalArgumentException("No bowling players available");
//            }
//
//            List<PlayerObject> batters = convertToPlayerObjects(battingPlayers);
//            List<PlayerObject> bowlers = convertToPlayerObjects(bowlingPlayers);
//
//            if (batters.size() < 2) {
//                throw new IllegalArgumentException("Not enough batters available");
//            }
//            if (bowlers.isEmpty()) {
//                throw new IllegalArgumentException("No bowlers available");
//            }
//
//            Queue<PlayerObject> availableBatters = new LinkedList<>(batters);
//            Queue<PlayerObject> availableBowlers = new LinkedList<>(bowlers);
//
//            PlayerObject striker = availableBatters.poll();
//            PlayerObject nonStriker = availableBatters.poll();
//            PlayerObject bowler = availableBowlers.poll();
//
//            int bowlerIndex = 0;
//            int totalOvers = innings.getTotalOvers();
//            int ballsPlayed = 0;
//            int oversCompleted = 0;
//            int ballsInCurrentOver = 0;
//
//            while (innings.getWickets() < 10 && innings.getRuns() < 200 && ballsPlayed < totalOvers * 6) {
//                if (ballsInCurrentOver == 6) {
//                    if (bowler != null) {
//                        bowler.addOverBowled();
//                    }
//                    oversCompleted++;
//                    ballsInCurrentOver = 0;
//                    System.out.println("Over " + oversCompleted + " End");
//
//                    // Rotate bowlers
//                    if (!availableBowlers.isEmpty()) {
//                        bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
//                        bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
//                    } else {
//                        System.out.println("No more bowlers available.");
//                        break;
//                    }
//                }
//
//                ballsPlayed++;
//                ballsInCurrentOver++;
//
//                System.out.println("Over " + (oversCompleted + 1) + " Ball " + ballsInCurrentOver);
//                if (bowler != null) {
//                    System.out.println("Bowler: " + bowler.getPlayer().getName());
//                }
//                if (striker != null) {
//                    System.out.println("Striker: " + striker.getPlayer().getName());
//                }
//                if (nonStriker != null) {
//                    System.out.println("Non-Striker: " + nonStriker.getPlayer().getName());
//                }
//
//                BallOutcome outcome = simulateBallEvent(innings, striker, nonStriker, bowler);
//
//                // Print ball details
//                StringBuilder ballDetail = new StringBuilder("Ball Details: Runs Scored: " + outcome.getRuns());
//                if (outcome.isWicket()) {
//                    ballDetail.append(", Wicket: ").append(outcome.getWicketType())
//                            .append(" by ").append(outcome.getBowler().getPlayer().getName());
//                }
//                if (outcome.getBallType() == BallType.NO_BALL) {
//                    ballDetail.append(", No Ball");
//                } else if (outcome.getBallType() == BallType.WIDE) {
//                    ballDetail.append(", Wide");
//                } else if (outcome.getBallType() == BallType.BOUNCER) {
//                    ballDetail.append(", Bouncer");
//                }
//                System.out.println(ballDetail.toString());
//
//                // Update players based on ball outcome
//                if (outcome.isWicket()) {
//                    innings.incrementWickets();
//                    System.out.println("Wicket! " + striker.getPlayer().getName() + " is out.");
//                    if (!availableBatters.isEmpty()) {
//                        striker = availableBatters.poll();  // Replace striker
//                    } else {
//                        striker = null;  // All batters are out
//                    }
//                } else {
//                    if (striker != null) {
//                        striker.setScore(striker.getScore() + outcome.getRuns());
//                    }
//                }
//
//                // Swap striker and non-striker if runs are odd
//                if (outcome.getRuns() % 2 != 0) {
//                    PlayerObject temp = striker;
//                    striker = nonStriker;
//                    nonStriker = temp;
//                }
//
//                TimeUnit.SECONDS.sleep(getDelayBasedOnEvent(innings.getRuns(), innings.getWickets()));
//
//                if (ballsInCurrentOver == 6) {
//                    bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
//                    bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
//                }
//            }
//
//            if (ballsInCurrentOver > 0) {
//                if (bowler != null) {
//                    bowler.addOverBowled();
//                }
//                oversCompleted++;
//            }
//            System.out.println("Innings End: " + innings.getRuns() + "/" + innings.getWickets() + " in " + oversCompleted + "." + ballsInCurrentOver);
//
//            System.out.println("Player Stats:");
//            for (PlayerObject player : batters) {
//                System.out.println(player.getPlayer().getName() + ": " +
//                        "Runs = " + player.getScore() +
//                        ", Balls Faced = " + player.getBallsFaced() +
//                        ", Fours = " + player.getFours() +
//                        ", Sixes = " + player.getSixes() +
//                        ", Dot Balls = " + player.getDotBalls() +
//                        ", Singles = " + player.getSingles() +
//                        ", Twos = " + player.getTwos() +
//                        ", Threes = " + player.getThrees() +
//                        ", Overs Bowled = " + player.getOversBowled() +
//                        ", Wickets Taken = " + player.getWicketsTaken()
//                );
//            }
//        }


//    private void simulateInnings(Innings innings) throws InterruptedException {
//        System.out.println("Innings Start: " + innings.getBattingTeam().getName());
//
//        List<Player> battingPlayers = innings.getBattingTeam().getPlayers();
//        List<Player> bowlingPlayers = innings.getBowlingTeam().getPlayers();
//
//        if (battingPlayers == null || battingPlayers.isEmpty()) {
//            throw new IllegalArgumentException("No batting players available");
//        }
//        if (bowlingPlayers == null || bowlingPlayers.isEmpty()) {
//            throw new IllegalArgumentException("No bowling players available");
//        }
//
//        List<PlayerObject> batters = convertToPlayerObjects(battingPlayers);
//        List<PlayerObject> bowlers = convertToPlayerObjects(bowlingPlayers);
//
//        if (batters.size() < 2) {
//            throw new IllegalArgumentException("Not enough batters available");
//        }
//        if (bowlers.isEmpty()) {
//            throw new IllegalArgumentException("No bowlers available");
//        }
//
//        Queue<PlayerObject> availableBatters = new LinkedList<>(batters);
//        Queue<PlayerObject> availableBowlers = new LinkedList<>(bowlers);
//
//        PlayerObject striker = availableBatters.poll();
//        PlayerObject nonStriker = availableBatters.poll();
//        PlayerObject bowler = availableBowlers.poll();
//
//        int bowlerIndex = 0;
//        int totalOvers = innings.getTotalOvers();
//        int ballsPlayed = 0;
//        int oversCompleted = 0;
//        int ballsInCurrentOver = 0;
//
//        while (innings.getWickets() < 10 && innings.getRuns() < 200 && ballsPlayed < totalOvers * 6) {
//            if (ballsInCurrentOver == 6) {
//                if (bowler != null) {
//                    bowler.addOverBowled();
//                }
//                oversCompleted++;
//                ballsInCurrentOver = 0;
//                System.out.println("Over " + oversCompleted + " End");
//
//                // Rotate bowlers
//                if (!availableBowlers.isEmpty()) {
//                    bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
//                    bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
//                } else {
//                    System.out.println("No more bowlers available.");
//                    break;
//                }
//            }
//
//            ballsPlayed++;
//            ballsInCurrentOver++;
//
//            System.out.println("Over " + (oversCompleted + 1) + " Ball " + ballsInCurrentOver);
//            if (bowler != null) {
//                System.out.println("Bowler: " + bowler.getPlayer().getName());
//            }
//            if (striker != null) {
//                System.out.println("Striker: " + striker.getPlayer().getName());
//            }
//            if (nonStriker != null) {
//                System.out.println("Non-Striker: " + nonStriker.getPlayer().getName());
//            }
//
//            BallOutcome outcome = simulateBallEvent(innings, striker, nonStriker, bowler);
//
//            // Print ball details
//            StringBuilder ballDetail = new StringBuilder("Ball Details: Runs Scored: " + outcome.getRuns());
//            if (outcome.isWicket()) {
//                ballDetail.append(", Wicket: ").append(outcome.getWicketType())
//                        .append(" by ").append(outcome.getBowler().getPlayer().getName());
//            }
//            if (outcome.getBallType() == BallType.NO_BALL) {
//                ballDetail.append(", No Ball");
//            } else if (outcome.getBallType() == BallType.WIDE) {
//                ballDetail.append(", Wide");
//            } else if (outcome.getBallType() == BallType.BOUNCER) {
//                ballDetail.append(", Bouncer");
//            }
//            System.out.println(ballDetail.toString());
//
//            // Update players based on ball outcome
//            if (outcome.isWicket()) {
//                innings.incrementWickets();
//                System.out.println("Wicket! " + striker.getPlayer().getName() + " is out.");
//                if (!availableBatters.isEmpty()) {
//                    striker = availableBatters.poll();  // Replace striker
//                } else {
//                    striker = null;  // All batters are out
//                }
//            } else {
//                if (striker != null) {
//                    striker.setScore(striker.getScore() + outcome.getRuns());
//                }
//            }
//
//            // Swap striker and non-striker if runs are odd
//            if (outcome.getRuns() % 2 != 0) {
//                PlayerObject temp = striker;
//                striker = nonStriker;
//                nonStriker = temp;
//            }
//
//            // Save player stats and team status at the end of each over or if the innings ends
//            if (ballsInCurrentOver == 6 || innings.getWickets() == 10 || innings.getRuns() >= 200) {
//                for (PlayerObject batter : batters) {
//                    updatePlayerStats(batter);
//                }
//                for (PlayerObject currBowler : bowlers) {
//                    updatePlayerStats(currBowler);
//                }
//                updateTeamStatus(innings);
//
//                if (ballsInCurrentOver == 6) {
//                    bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
//                    bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
//                }
//            }
//
//            TimeUnit.SECONDS.sleep(getDelayBasedOnEvent(innings.getRuns(), innings.getWickets()));
//        }
//
//        System.out.println("Innings End: " + innings.getRuns() + "/" + innings.getWickets() + " in " + oversCompleted + "." + ballsInCurrentOver);
//        System.out.println("Player Stats:");
//        for (PlayerObject player : batters) {
//            System.out.println(player.getPlayer().getName() + ": " +
//                    "Runs = " + player.getScore() +
//                    ", Balls Faced = " + player.getBallsFaced() +
//                    ", Fours = " + player.getFours() +
//                    ", Sixes = " + player.getSixes() +
//                    ", Dot Balls = " + player.getDotBalls() +
//                    ", Singles = " + player.getSingles() +
//                    ", Twos = " + player.getTwos() +
//                    ", Threes = " + player.getThrees());
//        }
//        for (PlayerObject player : bowlers) {
//            System.out.println(player.getPlayer().getName() + ": " +
//                    "Overs Bowled = " + player.getOversBowled() +
//                    ", Wickets = " + player.getWicketsTaken());
//        }
//    }
//    private void simulateInnings(Innings innings){
//        System.out.println("this method is called");
//        System.out.println("Match ID: " + innings.getId());
//        System.out.println("Team A: " + innings.getBattingTeam().getName());
//        System.out.println("Team B: " + innings.getBowlingTeam().getName());
//        System.out.println("Innings A: " + innings.getRuns());
//        System.out.println("Innings B: " + innings.getWickets());
//        System.out.println("Innings A: " + innings.getCurrentScore());
//        System.out.println("Innings B: " + innings.getCurrentWickets());
//        System.out.println("Innings A: " + innings.getCurrentOvers());
//        System.out.println("Innings B: " + innings.getCurrentOvers());
//        List<Player> battingPlayers = innings.getBattingTeam().getPlayers();
//
//        System.out.println("##################");
//        System.out.println();
//        System.out.println("Batting Players: " + battingPlayers.size());
//        for (Player battingPlayer : battingPlayers) {
//            System.out.println(battingPlayer.getName());
//        }
//        System.out.println("##################");
//        System.out.println();
//
//    }
    private  void simulateInnings(Innings innings) throws InterruptedException {
        System.out.println("Innings Start: " + innings.getBattingTeam().getName());

        List<Player> battingPlayers = innings.getBattingTeam().getPlayers();
        List<PlayerObject> batters = convertToPlayerObjects(battingPlayers);

        List<Player> bowlingPlayers = innings.getBowlingTeam().getPlayers();
        List<PlayerObject> bowlers = convertToPlayerObjects(bowlingPlayers);

        Queue<PlayerObject> availableBatters = new LinkedList<>(batters);
        Queue<PlayerObject> availableBowlers = new LinkedList<>(bowlers);

        System.out.println("Batters: " + batters.size());
        System.out.println("Bowlers: " + bowlers.size());
        System.out.println("Available Batters: " + availableBatters.size());
        System.out.println("Available Bowlers: " + availableBowlers.size());

        PlayerObject striker = availableBatters.poll();
        PlayerObject nonStriker = availableBatters.poll();
        PlayerObject bowler = availableBowlers.poll();

        assert striker != null;
        System.out.println("Striker: " + striker.getPlayer().getName());
        assert nonStriker != null;
        System.out.println("NonStriker: " + nonStriker.getPlayer().getName());
        assert bowler != null;
        System.out.println("Bowler: " + bowler.getPlayer().getName());

        int bowlerIndex = 0;
        int totalOvers = innings.getTotalOvers();
        int ballsPlayed = 0;
        int oversCompleted = 0;
        int ballsInCurrentOver = 0;

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println(innings.getBattingTeam().getName());
        System.out.println(innings.getBowlingTeam().getName());
        System.out.println(innings.getTotalOvers());
        System.out.println(innings.getWickets());
        System.out.println(innings.getRuns());





        while (innings.getWickets() < 10 && innings.getRuns() < 200) {
            System.out.println("testing this ");
            if (ballsInCurrentOver == 6) {
                bowler.addOverBowled();
                oversCompleted++;
                ballsInCurrentOver = 0;
                System.out.println("Over " + oversCompleted + " End");

                // Rotate bowlers
                bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
                bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
            }

            ballsPlayed++;
            ballsInCurrentOver++;

            System.out.println("Over " + (oversCompleted + 1) + " Ball " + ballsInCurrentOver);
            assert bowler != null;
            System.out.println("Bowler: " + bowler.getPlayer().getName());
            assert striker != null;
            System.out.println("Striker: " + striker.getPlayer().getName());
            assert nonStriker != null;
            System.out.println("Non-Striker: " + nonStriker.getPlayer().getName());

            BallOutcome outcome = simulateBallEvent(innings, striker, nonStriker, bowler);

            // Print ball details
            String ballDetail = "Ball Details: Runs Scored: " + outcome.getRuns();
            if (outcome.isWicket()) {
                ballDetail += ", Wicket: " + outcome.getWicketType() + " by " + outcome.getBowler().getPlayer().getName();
            }
            if (outcome.getBallType() == BallType.NO_BALL) {
                ballDetail += ", No Ball";
            } else if (outcome.getBallType() == BallType.WIDE) {
                ballDetail += ", Wide";
            } else if (outcome.getBallType() == BallType.BOUNCER) {
                ballDetail += ", Bouncer";
            }
            System.out.println(ballDetail);

            // Update players based on ball outcome
            if (outcome.isWicket()) {
                innings.incrementWickets();
                System.out.println("Wicket! " + striker.getPlayer().getName() + " is out.");
                if (!availableBatters.isEmpty()) {
                    striker = availableBatters.poll();  // Replace striker
                } else {
                    striker = null;  // All batters are out
                }
            }

            // Swap striker and non-striker if runs are odd
            if (outcome.getRuns() % 2 != 0) {
                PlayerObject temp = striker;
                striker = nonStriker;
                nonStriker = temp;
            }

            TimeUnit.SECONDS.sleep(getDelayBasedOnEvent(innings.getRuns(), innings.getWickets()));

            if (ballsInCurrentOver == 6) {
                bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
                bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
            }
        }

        if (ballsInCurrentOver > 0) {
            bowler.addOverBowled();
            oversCompleted++;
        }
        System.out.println("Innings End: " + innings.getRuns() + "/" + innings.getWickets() + " in " + oversCompleted + "." + ballsInCurrentOver);

        System.out.println("Player Stats:");
        for (PlayerObject player : batters) {
            System.out.println(player.getPlayer().getName() + ": " +
                    "Runs = " + player.getScore() +
                    ", Balls Faced = " + player.getBallsFaced() +
                    ", Fours = " + player.getFours() +
                    ", Sixes = " + player.getSixes() +
                    ", Dot Balls = " + player.getDotBalls() +
                    ", Singles = " + player.getSingles() +
                    ", Twos = " + player.getTwos() +
                    ", Threes = " + player.getThrees());
        }

        System.out.println("Bowler Stats:");
        for (PlayerObject currBowler : bowlers) {
            System.out.println(currBowler.getPlayer().getName() + ": " +
                    "Overs Bowled = " + currBowler.getOversBowled() +
                    ", Wickets Taken = " + currBowler.getWicketsTaken());
        }
    }



//    private void simulateInnings(Innings innings) throws InterruptedException {
//        System.out.println("Innings Start: " + innings.getBattingTeam().getName());
//        System.out.flush();
//
//        List<Player> battingPlayers = innings.getBattingTeam().getPlayers();
//        List<Player> bowlingPlayers = innings.getBowlingTeam().getPlayers();
//
//        if (battingPlayers == null || battingPlayers.isEmpty()) {
//            throw new IllegalArgumentException("No batting players available");
//        }
//        if (bowlingPlayers == null || bowlingPlayers.isEmpty()) {
//            throw new IllegalArgumentException("No bowling players available");
//        }
//
//        List<PlayerObject> batters = convertToPlayerObjects(battingPlayers);
//        List<PlayerObject> bowlers = convertToPlayerObjects(bowlingPlayers);
//
//        if (batters.size() < 2) {
//            throw new IllegalArgumentException("Not enough batters available");
//        }
//        if (bowlers.isEmpty()) {
//            throw new IllegalArgumentException("No bowlers available");
//        }
//
//        Queue<PlayerObject> availableBatters = new LinkedList<>(batters);
//        Queue<PlayerObject> availableBowlers = new LinkedList<>(bowlers);
//
//        PlayerObject striker = availableBatters.poll();
//        PlayerObject nonStriker = availableBatters.poll();
//        PlayerObject bowler = availableBowlers.poll();
//
//        int bowlerIndex = 0;
//        int totalOvers = innings.getTotalOvers();
//        int ballsPlayed = 0;
//        int oversCompleted = 0;
//        int ballsInCurrentOver = 0;
//
//        while (innings.getWickets() < 10 && innings.getRuns() < 200 && ballsPlayed < totalOvers * 6) {
//            if (ballsInCurrentOver == 6) {
//                if (bowler != null) {
//                    bowler.addOverBowled();
//                }
//                oversCompleted++;
//                ballsInCurrentOver = 0;
//                System.out.println("Over " + oversCompleted + " End");
//                System.out.flush();
//
//                // Rotate bowlers
//                if (!availableBowlers.isEmpty()) {
//                    bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
//                    bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
//                } else {
//                    System.out.println("No more bowlers available.");
//                    System.out.flush();
//                    break;
//                }
//            }
//
//            ballsPlayed++;
//            ballsInCurrentOver++;
//
//            System.out.println("Over " + (oversCompleted + 1) + " Ball " + ballsInCurrentOver);
//            System.out.flush();
//            if (bowler != null) {
//                System.out.println("Bowler: " + bowler.getPlayer().getName());
//                System.out.flush();
//            }
//            if (striker != null) {
//                System.out.println("Striker: " + striker.getPlayer().getName());
//                System.out.flush();
//            }
//            if (nonStriker != null) {
//                System.out.println("Non-Striker: " + nonStriker.getPlayer().getName());
//                System.out.flush();
//            }
//
//            BallOutcome outcome = simulateBallEvent(innings, striker, nonStriker, bowler);
//
//            // Print ball details
//            StringBuilder ballDetail = new StringBuilder("Ball Details: Runs Scored: " + outcome.getRuns());
//            if (outcome.isWicket()) {
//                ballDetail.append(", Wicket: ").append(outcome.getWicketType())
//                        .append(" by ").append(outcome.getBowler().getPlayer().getName());
//            }
//            if (outcome.getBallType() == BallType.NO_BALL) {
//                ballDetail.append(", No Ball");
//            } else if (outcome.getBallType() == BallType.WIDE) {
//                ballDetail.append(", Wide");
//            } else if (outcome.getBallType() == BallType.BOUNCER) {
//                ballDetail.append(", Bouncer");
//            }
//            System.out.println(ballDetail.toString());
//            System.out.flush();
//
//            // Update players based on ball outcome
//            if (outcome.isWicket()) {
//                innings.incrementWickets();
//                System.out.println("Wicket! " + striker.getPlayer().getName() + " is out.");
//                System.out.flush();
//                if (!availableBatters.isEmpty()) {
//                    striker = availableBatters.poll();  // Replace striker
//                } else {
//                    striker = null;  // All batters are out
//                }
//            } else {
//                if (striker != null) {
//                    striker.setScore(striker.getScore() + outcome.getRuns());
//                }
//            }
//
//            // Swap striker and non-striker if runs are odd
//            if (outcome.getRuns() % 2 != 0) {
//                PlayerObject temp = striker;
//                striker = nonStriker;
//                nonStriker = temp;
//            }
//
//            // Save player stats and team status at the end of each over or if the innings ends
//            if (ballsInCurrentOver == 6 || innings.getWickets() == 10 || innings.getRuns() >= 200) {
//                for (PlayerObject batter : batters) {
//                    updatePlayerStats(batter);
//                }
//                for (PlayerObject currBowler : bowlers) {
//                    updatePlayerStats(currBowler);
//                }
//                updateTeamStatus(innings);
//
//                if (ballsInCurrentOver == 6) {
//                    bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
//                    bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
//                }
//            }
//
//            TimeUnit.SECONDS.sleep(getDelayBasedOnEvent(innings.getRuns(), innings.getWickets()));
//        }
//
//        System.out.println("Innings End: " + innings.getRuns() + "/" + innings.getWickets() + " in " + oversCompleted + "." + ballsInCurrentOver);
//        System.out.flush();
//        System.out.println("Player Stats:");
//        System.out.flush();
//        for (PlayerObject player : batters) {
//            System.out.println(player.getPlayer().getName() + ": " +
//                    "Runs = " + player.getScore() +
//                    ", Balls Faced = " + player.getBallsFaced() +
//                    ", Fours = " + player.getFours() +
//                    ", Sixes = " + player.getSixes() +
//                    ", Dot Balls = " + player.getDotBalls() +
//                    ", Singles = " + player.getSingles() +
//                    ", Twos = " + player.getTwos() +
//                    ", Threes = " + player.getThrees());
//            System.out.flush();
//        }
//        for (PlayerObject player : bowlers) {
//            System.out.println(player.getPlayer().getName() + ": " +
//                    "Overs Bowled = " + player.getOversBowled() +
//                    ", Wickets = " + player.getWicketsTaken());
//            System.out.flush();
//        }
//    }


    private void updatePlayerStats(PlayerObject playerObject) {
        Player player = playerObject.getPlayer();

        // Update player stats
        player.setRuns(player.getRuns() + playerObject.getScore());
        player.setBallsFaced(player.getBallsFaced() + playerObject.getBallsFaced());
        player.setFours(player.getFours() + playerObject.getFours());
        player.setSixes(player.getSixes() + playerObject.getSixes());
        player.setDotBalls(player.getDotBalls() + playerObject.getDotBalls());
        player.setSingles(player.getSingles() + playerObject.getSingles());
        player.setTwos(player.getTwos() + playerObject.getTwos());
        player.setThrees(player.getThrees() + playerObject.getThrees());
        player.setWicketsTaken(player.getWicketsTaken() + playerObject.getWicketsTaken());
        player.setOversBowled(player.getOversBowled() + playerObject.getOversBowled());

        // Save updated player stats to the database
        playerRepository.save(player);
    }

    private void updateTeamStatus(Innings innings) {
        Team battingTeam = innings.getBattingTeam();

        // Update team status with innings stats
        battingTeam.setRunsScored((long) innings.getRuns());
        battingTeam.setWicketsLost(innings.getWickets());
        battingTeam.setOversPlayed(Double.valueOf(innings.getCurrentOvers()));

        // Save updated team status to the database
        teamRepository.save(battingTeam);
    }


    public  List<PlayerObject> convertToPlayerObjects(List<Player> players) {
            return players.stream()
                    .map(PlayerObject::new) // Use the constructor to create PlayerObject
                    .collect(Collectors.toList());
        }

    private BallOutcome simulateBallEvent(Innings innings, PlayerObject striker, PlayerObject nonStriker, PlayerObject bowler) {
        Random rand = new Random();
        BallType ballType = BallType.NORMAL;
        int ballTypeIndex = rand.nextInt(BallType.values().length);
        ballType = BallType.values()[ballTypeIndex];

        int runs = 0;
        boolean isWicket = false;

        switch (ballType) {
            case NO_BALL:
            case WIDE:
                isWicket = false;
                runs = 1;
                break;
            case BOUNCER:
            case NORMAL:
                runs = rand.nextInt(7);
                break;
        }

        Ball ball = new Ball();
        ball.setBallType(ballType);
        ball.setBallSpeed(rand.nextDouble() * 100); // Example speed
        ball.setPlayedBy(striker);
        ball.setBowledBy(bowler);
        ball.setRun(runs);

        if (ballType != BallType.NO_BALL && rand.nextInt(10) < 2) {
            isWicket = true;
            int wicketTypeIndex = rand.nextInt(WicketType.values().length);
            WicketType wicketType = WicketType.values()[wicketTypeIndex];
            ball.setWicket(new Wicket(wicketType)); // Assuming Wicket is another entity
            bowler.addWicket();
        } else {
            innings.addRuns(runs);
            if (runs == 0 && ballType == BallType.NORMAL) {
                striker.addDotBall();
            }
        }
        ball.setId(1L);
        // Save ball event
        ballRepository.save(ball);

        // Print ball details
        String ballDetail = "Ball Details: Runs Scored: " + runs + ", Ball Type: " + ballType;
        if (isWicket) {
            ballDetail += ", Wicket: " + ball.getWicket().getWicketType() + " by " + bowler.getPlayer().getName();
        }
        System.out.println(ballDetail);

        // Safely handle the case where WicketType might be null
        WicketType wicketType = (ball.getWicket() != null) ? ball.getWicket().getWicketType() : null;

        return new BallOutcome(runs, isWicket, wicketType, bowler, ballType);
    }

        private void printMatchResult(Innings inningsA, Innings inningsB) {
            int scoreA = inningsA.getRuns();
            int scoreB = inningsB.getRuns();

            String result;
            if (scoreA > scoreB) {
                result = inningsA.getBattingTeam().getName() + " won by " + (scoreA - scoreB) + " runs";
            } else if (scoreB > scoreA) {
                result = inningsB.getBattingTeam().getName() + " won by " + (10 - inningsB.getWickets()) + " wickets";
            } else {
                result = "Match tied";
            }

            System.out.println("Match Result: " + result);
        }

        private static long getDelayBasedOnEvent(int runs, int wickets) {
            if (wickets >= 1) {
                return 3;
            } else if (runs % 6 == 0) {
                return 2;
            } else {
                return 1;
            }
        }

}


