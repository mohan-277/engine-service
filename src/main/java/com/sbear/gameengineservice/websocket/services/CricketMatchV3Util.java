package com.sbear.gameengineservice.websocket.services;



import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.Team;
import com.sbear.gameengineservice.repository.CricketMatchRepository;
import com.sbear.gameengineservice.repository.InningsRepository;
import com.sbear.gameengineservice.repository.PlayerRepository;
import com.sbear.gameengineservice.repository.TeamRepository;
import com.sbear.gameengineservice.utilities.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CricketMatchV3Util {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private CricketMatchRepository cricketMatchRepository;
    @Autowired
    private InningsRepository inningsRepository;



    @Transactional
    public CricketMatchUtil convertDTOToCricketMatchUtil(MatchDetailsDTO matchDTO) {
        // Fetch teams from repository
        System.out.println(matchDTO.getTeamA());
        Team teamA = teamRepository.findTeamByName(matchDTO.getTeamA());
        System.out.println(teamA.getName());
        Team teamB = teamRepository.findTeamByName(matchDTO.getTeamB());
        System.out.println(teamB.getName());

        // Fetch players for each team from repository
        List<Player> playersA = playerRepository.findByTeamId(teamA.getId());
        List<Player> playersB = playerRepository.findByTeamId(teamB.getId());

//        if (playersA.size() != 15 || playersB.size() != 15) {
//            throw new IllegalArgumentException("Each team must have exactly 15 players.");
//        }

        // Initialize TeamUtil with the fetched players
        TeamUtil teamUtilA = new TeamUtil(teamA.getName());
        TeamUtil teamUtilB = new TeamUtil(teamB.getName());

        for (Player player : playersA) {
            teamUtilA.addPlayer(new PlayerUtil(player.getName(), player.getDateOfBirth(),
                    player.getSpecialization(), player.getGender(),
                    player.getCountry()));
        }

        for (Player player : playersB) {
            teamUtilB.addPlayer(new PlayerUtil(player.getName(), player.getDateOfBirth(),
                    player.getSpecialization(), player.getGender(),
                    player.getCountry()));
        }

        // Create and initialize CricketMatchUtil
        return new CricketMatchUtil(
                teamUtilA,
                teamUtilB,
                matchDTO.getMatchType(),
                matchDTO.getMatchId(),
                matchDTO.getMatchDateTime()// Assuming you have a totalOvers field in MatchDetailsDTO
        );
    }


    @Transactional
    public void simulateMatchFromDTO(MatchDetailsDTO matchDTO) throws InterruptedException {

        simulateMatch(convertDTOToCricketMatchUtil(matchDTO));
    }




    public static void simulateMatch(CricketMatchUtil match) throws InterruptedException {
        System.out.println("Match Started: " + match.getTeamA().getName() + " vs " + match.getTeamB().getName());
        System.out.println("Match Type: " + match.getMatchType());

        // Simulate the innings
        InningsUtil inningsUtilA = new InningsUtil(match, match.getTeamA(), match.getTeamB(),1L);
        InningsUtil inningsUtilB = new InningsUtil(match, match.getTeamB(), match.getTeamA(),2L);

        simulateInnings(inningsUtilA);
        simulateInnings(inningsUtilB);

        // Print match result
        printMatchResult(inningsUtilA, inningsUtilB);
    }

//    private static void simulateInnings(Innings innings) throws InterruptedException {
//        System.out.println("Innings Start: " + innings.getBattingTeam().getName());
//
//        // Convert list of Player to list of PlayerObject
//        List<Player> battingPlayers = innings.getBattingTeam().getPlayers();
//        List<PlayerObject> batters = convertToPlayerObjects(battingPlayers);
//
//        List<Player> bowlingPlayers = innings.getBowlingTeam().getPlayers();
//        List<PlayerObject> bowlers = convertToPlayerObjects(bowlingPlayers);
//
//        // Initialize available batters and bowlers
//        Queue<PlayerObject> availableBatters = new LinkedList<>(batters);
//        Queue<PlayerObject> availableBowlers = new LinkedList<>(bowlers);
//
//        // Set initial players
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
//                oversCompleted++;
//                ballsInCurrentOver = 0;
//                System.out.println("Over " + oversCompleted + " End");
//                // Rotate bowlers
//                bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
//                bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
//            }
//
//            ballsPlayed++;
//            ballsInCurrentOver++;
//
//            System.out.println("Over " + (oversCompleted + 1) + " Ball " + ballsInCurrentOver);
//            System.out.println("Bowler: " + bowler.getPlayer().getName());
//            System.out.println("Striker: " + striker.getPlayer().getName());
//            System.out.println("Non-Striker: " + nonStriker.getPlayer().getName());
//
//            BallOutcome outcome = simulateBallEvent(innings, striker, nonStriker, bowler);
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
//                striker.addScore(outcome.getRuns());
//            }
//
//            // Swap striker and non-striker if runs are odd
//            if (outcome.getRuns() % 2 != 0) {
//                PlayerObject temp = striker;
//                striker = nonStriker;
//                nonStriker = temp;
//            }
//
//            TimeUnit.SECONDS.sleep(getDelayBasedOnEvent(innings.getRuns(), innings.getWickets()));
//
//            if (ballsInCurrentOver == 6) {
//                bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
//                bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
//            }
//        }
//
//        // Account for last over if it was not completed
//        if (ballsInCurrentOver > 0) {
//            oversCompleted++;
//        }
//        System.out.println("Innings End: " + innings.getRuns() + "/" + innings.getWickets() + " in " + oversCompleted + "." + ballsInCurrentOver);
//    }


    public static void simulateInnings(InningsUtil inningsUtil) throws InterruptedException {
        System.out.println("Innings Start CricketMatch utilV3: " + inningsUtil.getBattingTeam().getName());

        List<PlayerUtil> battingPlayerUtils = inningsUtil.getBattingTeam().getPlayers();
        List<PlayerObject> batters = convertToPlayerObjects(battingPlayerUtils);
        System.out.println(batters.size()+" ");
        System.out.println(batters.get(0).getPlayer().getName()+ " batter player Name");

        List<PlayerUtil> bowlingPlayerUtils = inningsUtil.getBowlingTeam().getPlayers();
        List<PlayerObject> bowlers = convertToPlayerObjects(bowlingPlayerUtils);

        Queue<PlayerObject> availableBatters = new LinkedList<>(batters);
        Queue<PlayerObject> availableBowlers = new LinkedList<>(bowlers);

        PlayerObject striker = availableBatters.poll();
        PlayerObject nonStriker = availableBatters.poll();
        PlayerObject bowler = availableBowlers.poll();

        int bowlerIndex = 0;
        int totalOvers = inningsUtil.getTotalOvers();
        int ballsPlayed = 0;
        int oversCompleted = 0;
        int ballsInCurrentOver = 0;

        while (inningsUtil.getWickets() < 10 && inningsUtil.getRuns() < 200 && ballsPlayed < totalOvers * 6) {
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

            BallOutcomeUtil outcome = simulateBallEvent(inningsUtil, striker, nonStriker, bowler);

            // Print ball details
            String ballDetail = "Ball Details: Runs Scored: " + outcome.getRuns();
            if (outcome.isWicket()) {
                ballDetail += ", Wicket: " + outcome.getWicketType() + " by " + outcome.getBowler().getPlayer().getName();
            }
            if (outcome.getBallType() == BallTypeUtil.NO_BALL) {
                ballDetail += ", No Ball";
            } else if (outcome.getBallType() == BallTypeUtil.WIDE) {
                ballDetail += ", Wide";
            } else if (outcome.getBallType() == BallTypeUtil.BOUNCER) {
                ballDetail += ", Bouncer";
            }
            System.out.println(ballDetail);

            // Update players based on ball outcome
            if (outcome.isWicket()) {
                inningsUtil.incrementWickets();
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

            TimeUnit.SECONDS.sleep(getDelayBasedOnEvent(inningsUtil.getRuns(), inningsUtil.getWickets()));

            if (ballsInCurrentOver == 6) {
                bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
                bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
            }
        }

        if (ballsInCurrentOver > 0) {
            bowler.addOverBowled();
            oversCompleted++;
        }
        System.out.println("Innings End: " + inningsUtil.getRuns() + "/" + inningsUtil.getWickets() + " in " + oversCompleted + "." + ballsInCurrentOver);

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

    static List<PlayerObject> convertToPlayerObjects(List<PlayerUtil> playerUtils) {
        List<PlayerObject> playerObjects = new ArrayList<>();
        for (PlayerUtil playerUtil : playerUtils) {
            playerObjects.add(new PlayerObject(playerUtil));
        }
        return playerObjects;
    }

    private static PlayerObject getRandomPlayer(List<PlayerObject> players) {
        Random random = new Random();
        return players.get(random.nextInt(players.size()));
    }

    private static long getDelayBasedOnEvent(int runs, int wickets) {
        if (wickets >= 1) {
            return 3; // Longer delay for a wicket
        } else if (runs % 6 == 0) {
            return 2; // Delay for a six
        } else {
            return 1; // Normal delay
        }
    }

//    private static BallOutcome simulateBallEvent(Innings innings, PlayerObject striker, PlayerObject nonStriker, PlayerObject bowler) {
//        // Simulate the outcome of a ball
//        Random rand = new Random();
//        int runs = rand.nextInt(7);  // Runs scored can be from 0 to 6
//        boolean isWicket = rand.nextInt(10) < 2;  // 20% chance of a wicket
//
//        if (isWicket) {
//            // Simulate a wicket type
//            int wicketType = rand.nextInt(2);  // 0 for catch out, 1 for bowled
//            return new BallOutcome(runs, true, wicketType == 0 ? "CAUGHT_ON_BOWLED" : "CATCH_OUT", bowler);
//        } else {
//            return new BallOutcome(runs, false, "", null);
//        }
//    }




    private static BallOutcomeUtil simulateBallEvent(InningsUtil inningsUtil, PlayerObject striker, PlayerObject nonStriker, PlayerObject bowler) {
        Random rand = new Random();
        BallTypeUtil ballTypeUtil = BallTypeUtil.NORMAL;

        // Randomly determine ball type
        int ballTypeIndex = rand.nextInt(BallTypeUtil.values().length);
        ballTypeUtil = BallTypeUtil.values()[ballTypeIndex];

        int runs = 0;
        boolean isWicket = false;

        // Handle ball types
        switch (ballTypeUtil) {
            case NO_BALL:
                isWicket = false; // Wicket can't be taken on a no-ball
                runs = 1; // No-ball gives 1 extra run
                break;
            case WIDE:
                isWicket = false; // Wicket can't be taken on a wide
                runs = 1; // Wide gives 1 extra run
                break;
            case BOUNCER:
                runs = rand.nextInt(7); // Bouncer can have runs from 0 to 6
                break;
            case NORMAL:
                runs = rand.nextInt(7); // Normal ball can have runs from 0 to 6
                break;
        }

        if (ballTypeUtil != BallTypeUtil.NO_BALL && rand.nextInt(10) < 2) { // 20% chance of a wicket for normal balls
            isWicket = true;
            int wicketTypeIndex = rand.nextInt(WicketTypeUtil.values().length);
            WicketTypeUtil wicketTypeUtil = WicketTypeUtil.values()[wicketTypeIndex];
            bowler.addWicket();
            return new BallOutcomeUtil(runs, true, wicketTypeUtil, bowler, ballTypeUtil);
        } else {
            inningsUtil.addRuns(runs);
            if (runs == 0 && ballTypeUtil == BallTypeUtil.NORMAL) {
                striker.addDotBall(); // Add dot ball to striker
            }
            striker.addScore(runs); // Add runs to striker
            striker.addBallFaced(); // Increment balls faced
            return new BallOutcomeUtil(runs, false, null, null, ballTypeUtil);
        }
    }

    static void printMatchResult(InningsUtil inningsUtilA, InningsUtil inningsUtilB) {
        int scoreA = inningsUtilA.getRuns();
        int scoreB = inningsUtilB.getRuns();

        String result;
        if (scoreA > scoreB) {
            result = inningsUtilA.getBattingTeam().getName() + " won by " + (scoreA - scoreB) + " runs";
        } else if (scoreB > scoreA) {
            result = inningsUtilB.getBattingTeam().getName() + " won by " + (10 - inningsUtilB.getWickets()) + " wickets";
        } else {
            result = "Match tied";
        }

        System.out.println("Match Result: " + result);
    }
}
