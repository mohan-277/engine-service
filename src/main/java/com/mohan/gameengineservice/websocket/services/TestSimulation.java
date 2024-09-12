package com.mohan.gameengineservice.websocket.services;

import com.mohan.gameengineservice.dto.MatchDetailsDTO;
import com.mohan.gameengineservice.entity.Player;
import com.mohan.gameengineservice.entity.Team;
import com.mohan.gameengineservice.entity.stats.PlayerStats;
import com.mohan.gameengineservice.entity.stats.TeamStats;
import com.mohan.gameengineservice.repository.InningsRepository;
import com.mohan.gameengineservice.repository.PlayerRepository;
import com.mohan.gameengineservice.repository.TeamRepository;
import com.mohan.gameengineservice.repository.stats.PlayerStatsRepository;
import com.mohan.gameengineservice.repository.stats.TeamStatsRepository;
import com.mohan.gameengineservice.utilities.*;
import com.mohan.gameengineservice.websocket.WebSocketSessionManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class TestSimulation {

        @Autowired
        private TeamRepository teamRepository;
        @Autowired
        private PlayerRepository playerRepository;
        
        @Autowired
        private PlayerStatsRepository playerStatsRepository;
        
        @Autowired
        private TeamStatsRepository teamStatsRepository;

        @Autowired
        private InningsRepository inningsRepository;


        @Autowired
        private WebSocketSessionManager webSocketSessionManager; // WebSocket session reference



        private void sendMessage(String message) {
            webSocketSessionManager.sendMessage(message);
        }

        @Transactional
        public CricketMatchUtil convertDTOToCricketMatchUtil(MatchDetailsDTO matchDTO) {
            // Fetch teams from repository
            Team teamA = teamRepository.findTeamByName(matchDTO.getTeamA());
            Team teamB = teamRepository.findTeamByName(matchDTO.getTeamB());

            // Fetch players for each team from repository
            List<Player> playersA = playerRepository.findByTeamId(teamA.getId());
            List<Player> playersB = playerRepository.findByTeamId(teamB.getId());

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
                    "T20", // Assuming you have a totalOvers field in MatchDetailsDTO
                    matchDTO.getMatchId(),
                    matchDTO.getMatchDateTime()
            );
        }

        @Transactional
        public void simulateMatchFromDTO(MatchDetailsDTO matchDTO) throws InterruptedException, IOException {


            // this is for initial creating the teamStats
            createOrUpdateTeamStats(matchDTO,matchDTO.getTeamA());
            createOrUpdateTeamStats(matchDTO,matchDTO.getTeamB());

            simulateMatch(convertDTOToCricketMatchUtil(matchDTO));

        }

        private void createOrUpdateTeamStats(MatchDetailsDTO matchDTO, String teamName) {
            TeamStats teamStats = teamStatsRepository.findTeamStatsByMatchIdAndTeamName(matchDTO.getMatchId(), teamName);

            if (teamStats == null) {
                // Create new TeamStats if not exists
                teamStats = new TeamStats();
                teamStats.setMatchId(matchDTO.getMatchId());
                teamStats.setTeamName(teamName);
                teamStats.setLocation(matchDTO.getLocation());
                teamStats.setMatchType(matchDTO.getMatchType());
                teamStats.setMatchStage(matchDTO.getMatchStage());
                teamStats.setMatchGroup(matchDTO.getMatchGroup());
                teamStats.setMatchDateTime(matchDTO.getMatchDateTime());
                teamStats.setLive(matchDTO.isLive());
                teamStats.setRuns(0);
                teamStats.setWickets(0);
                teamStats.setOversCompleted(0);
                teamStats.setBallsInCurrentOver(0);
                teamStats.setTeamScore(0);
                teamStats.setMatchesPlayed(0);
                teamStats.setNumberOfLosses(0);
                teamStats.setBallsInCurrentOver(0);
            }

            // Save the newly created TeamStats
            teamStatsRepository.save(teamStats);
        }

        public void simulateMatch(CricketMatchUtil match) throws InterruptedException, IOException {
            sendMessage("Match Started: " + match.getTeamA().getName() + " vs " + match.getTeamB().getName());
            sendMessage("Match Type: " + match.getMatchType());

            // Simulate the innings
            InningsUtil inningsUtilA = new InningsUtil(match, match.getTeamA(), match.getTeamB());
            InningsUtil inningsUtilB = new InningsUtil(match, match.getTeamB(), match.getTeamA());


            Thread.sleep(TimeUnit.SECONDS.toMillis(5));
            simulateInnings(inningsUtilA, match.getMatchId());

            Thread.sleep(TimeUnit.SECONDS.toMillis(5));

            simulateInnings(inningsUtilB,match.getMatchId());

            Thread.sleep(TimeUnit.SECONDS.toMillis(5));
            // Print match result
            printMatchResult(inningsUtilA, inningsUtilB);
        }

        public void simulateInnings(InningsUtil inningsUtil, Long matchId) throws InterruptedException, IOException {
            sendMessage("batting Team : " + inningsUtil.getBattingTeam().getName());
            sendMessage("bowling Team: " + inningsUtil.getBowlingTeam().getName());

            List<PlayerUtil> battingPlayerUtils = inningsUtil.getBattingTeam().getPlayers();
            List<PlayerObject> batters = convertToPlayerObjects(battingPlayerUtils);

            List<PlayerUtil> bowlingPlayerUtils = inningsUtil.getBowlingTeam().getPlayers();
            List<PlayerObject> bowlers = convertToPlayerObjects(bowlingPlayerUtils);

            Queue<PlayerObject> availableBatters = new LinkedList<>(batters);
            Queue<PlayerObject> availableBowlers = new LinkedList<>(bowlers);

            PlayerObject striker = availableBatters.poll();
            PlayerObject nonStriker = availableBatters.poll();
            PlayerObject bowler = availableBowlers.poll();

            if (striker == null) {
                System.err.println("Error: Striker is null.");
                return;
            }
            if (nonStriker == null) {
                System.err.println("Error: Non-striker is null.");
                return;
            }
            if (bowler == null) {
                System.err.println("Error: Bowler is null.");
                return;
            }

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
                    sendMessage("Over " + oversCompleted + " End");

                    // Rotate bowlers
                    bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
                    bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
                }

                ballsPlayed++;
                ballsInCurrentOver++;

                sendMessage("Over " + (oversCompleted + 1) + " Ball " + ballsInCurrentOver);
                assert bowler != null;
                sendMessage("Bowler: " + bowler.getPlayer().getName());
                assert striker != null;
                sendMessage("Striker: " + striker.getPlayer().getName());
                assert nonStriker != null;
                sendMessage("Non-Striker: " + nonStriker.getPlayer().getName());

//                sendMessage("Non-Striker: " + nonStriker.getPlayer().getSpecialization());

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
                sendMessage(ballDetail);

                // Update players based on ball outcome
                if (outcome.isWicket()) {
                    inningsUtil.incrementWickets();
                    sendMessage("Wicket " + striker.getPlayer().getName() + " is out.");
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
            sendMessage("Innings End: " + inningsUtil.getBattingTeam().getName() + "  " + inningsUtil.getRuns() + "/" + inningsUtil.getWickets() + " in " + oversCompleted + "." + ballsInCurrentOver);

            // updating the team stats

            TeamStats teamStats = teamStatsRepository.findTeamStatsByMatchIdAndTeamName(inningsUtil.getMatch().getMatchId(), inningsUtil.getBattingTeam().getName());
//            if (teamStats== null) {
//                teamStats.setRuns(0);
//
//                teamStats.setWickets(0);
//
//                teamStats.setOversCompleted(0);
//
//                teamStats.setTeamScore(0);
//
//                teamStats.setBallsInCurrentOver(0);
//            }
//
////            if (teamStats.getRuns() == null) {
//                teamStats.setRuns(0);
//            }
//            if (teamStats.getWickets() == null) {
//                teamStats.setWickets(0);
//            }
//            if (teamStats.getOversCompleted() == null) {
//                teamStats.setOversCompleted(0);
//            }
//            if (teamStats.getTeamScore() == null) {
//                teamStats.setTeamScore(0);
//            }
//            if (teamStats.getBallsInCurrentOver() == null) {
//                teamStats.setBallsInCurrentOver(0);
//            }

//            teamStats.setRuns(Optional.of(inningsUtil.getRuns()).orElse(0));
//            teamStats.setWickets(Optional.of(inningsUtil.getWickets()).orElse(0));
//            teamStats.setOversCompleted(oversCompleted);
//            teamStats.setBallsInCurrentOver(ballsInCurrentOver);
//            teamStats.setTeamScore(inningsUtil.getRuns());// Total score of the team


            if (teamStats == null) {
                // Create new TeamStats if not exists
                teamStats = new TeamStats();
                teamStats.setMatchId(inningsUtil.getMatch().getMatchId());
                teamStats.setTeamName(inningsUtil.getBattingTeam().getName());
                teamStats.setRuns(0);
                teamStats.setWickets(0);
                teamStats.setOversCompleted(0);
                teamStats.setBallsInCurrentOver(0);
                teamStats.setTeamScore(0);
                teamStats.setMatchesPlayed(0);
                teamStats.setNumberOfLosses(0);
                // Save the newly created TeamStats
                teamStatsRepository.save(teamStats);
            }

            // Update existing TeamStats
            teamStats.setRuns(inningsUtil.getRuns());
            teamStats.setWickets(inningsUtil.getWickets());
            teamStats.setOversCompleted(oversCompleted);
            teamStats.setBallsInCurrentOver(ballsInCurrentOver);
            teamStats.setTeamScore(inningsUtil.getRuns());

//            teamStats.setResult("Result"); // Set based on actual match result
//            teamStats.setPoints(2); // Calculate points based on match result
//            teamStats.setMatchesPlayed(1); // Increment if this is a new match
            teamStatsRepository.save(teamStats);

            sendMessage("Player Stats:");
            for (PlayerObject player : batters) {
                sendMessage(player.getPlayer().getName() + ": " +
                        "Runs = " + player.getScore() +
                        ", Balls Faced = " + player.getBallsFaced() +
                        ", Fours = " + player.getFours() +
                        ", Sixes = " + player.getSixes() +
                        ", Dot Balls = " + player.getDotBalls() +
                        ", Singles = " + player.getSingles() +
                        ", Twos = " + player.getTwos() +
                        ", Threes = " + player.getThrees());
            }

            for (PlayerObject currBatter : batters) {
                PlayerStats newStats = getBatterStats(inningsUtil, currBatter,matchId);
//                PlayerStats existingStats = playerStatsRepository.findByCurrentPlayingMatchIdAndPlayerName(matchId, currBatter.getPlayer().getName());

//                if (existingStats != null) {
//                    // Update existing record
//                    existingStats.setRuns(newStats.getRuns());
//                    existingStats.setBallsFaced(newStats.getBallsFaced());
//                    existingStats.setFours(newStats.getFours());
//                    existingStats.setSixes(newStats.getSixes());
//                    existingStats.setDotBalls(newStats.getDotBalls());
//                    existingStats.setSingles(newStats.getSingles());
//                    existingStats.setTwos(newStats.getTwos());
//                    existingStats.setThrees(newStats.getThrees());
//                    // Other fields you want to update
//                    playerStatsRepository.save(existingStats);
//                } else {
//                    // Save new record
//                    playerStatsRepository.save(newStats);
//                }
                playerStatsRepository.save(newStats);
            }

            sendMessage("Bowler Stats:");
            for (PlayerObject currBowler : bowlers) {
                sendMessage(currBowler.getPlayer().getName() + ": " +
                        "Overs Bowled = " + currBowler.getOversBowled() +
                        ", Wickets Taken = " + currBowler.getWicketsTaken());
            }

            for (PlayerObject currBowler : bowlers) {
                PlayerStats bowlerStats = getBowlerStats(inningsUtil, currBowler,matchId);
                playerStatsRepository.save(bowlerStats);
//                PlayerStats newStats = getBowlerStats(inningsUtil, currBowler,matchId);
//                PlayerStats existingStats = playerStatsRepository.findByCurrentPlayingMatchIdAndPlayerName(matchId, currBowler.getPlayer().getName());
//
//                if (existingStats != null) {
//                    // Update existing record
//                    existingStats.setRuns(newStats.getRuns());
//                    existingStats.setBallsFaced(newStats.getBallsFaced());
//                    existingStats.setFours(newStats.getFours());
//                    existingStats.setSixes(newStats.getSixes());
//                    existingStats.setDotBalls(newStats.getDotBalls());
//                    existingStats.setSingles(newStats.getSingles());
//                    existingStats.setTwos(newStats.getTwos());
//                    existingStats.setThrees(newStats.getThrees());
//                    // Other fields you want to update
//                    playerStatsRepository.save(existingStats);
//                } else {
//                    // Save new record
//                    playerStatsRepository.save(newStats);
//                }
            }



        }

    private static PlayerStats getBowlerStats(InningsUtil inningsUtil, PlayerObject currBowler,Long matchId) {
        PlayerStats bowlerStats = new PlayerStats();
        bowlerStats.setPlayerName(currBowler.getPlayer().getName());
        bowlerStats.setTeamName(inningsUtil.getBowlingTeam().getName());
        bowlerStats.setPlayerType(currBowler.getPlayer().getSpecialization()); // Assume you have player type
        bowlerStats.setRuns(0); // Not relevant for bowlers
        bowlerStats.setBallsFaced(0); // Not relevant for bowlers
        bowlerStats.setFours(0); // Not relevant for bowlers
        bowlerStats.setSixes(0); // Not relevant for bowlers
        bowlerStats.setDotBalls(0); // Not relevant for bowlers
        bowlerStats.setSingles(0); // Not relevant for bowlers
        bowlerStats.setTwos(0); // Not relevant for bowlers
        bowlerStats.setThrees(0); // Not relevant for bowlers
        bowlerStats.setOversBowled(currBowler.getOversBowled());
        bowlerStats.setWicketsTaken(currBowler.getWicketsTaken());
        bowlerStats.setCurrentPlayingMatchId(matchId);
        return bowlerStats;
    }

    private static PlayerStats getBatterStats(InningsUtil inningsUtil, PlayerObject player, Long matchId) {
        PlayerStats playerStats = new PlayerStats();
        playerStats.setPlayerName(player.getPlayer().getName());
        playerStats.setTeamName(inningsUtil.getBattingTeam().getName());
        playerStats.setPlayerType(player.getPlayer().getSpecialization()); // Assume you have player type
        playerStats.setRuns(player.getScore());
        playerStats.setBallsFaced(player.getBallsFaced());
        playerStats.setFours(player.getFours());
        playerStats.setSixes(player.getSixes());
        playerStats.setDotBalls(player.getDotBalls());
        playerStats.setSingles(player.getSingles());
        playerStats.setTwos(player.getTwos());
        playerStats.setThrees(player.getThrees());
        playerStats.setCurrentPlayingMatchId(matchId);
        playerStats.setOversBowled(0); // Not relevant for batsmen
        playerStats.setWicketsTaken(0); // Not relevant for batsmen
        return playerStats;
    }


    /* this is trying to build the over wide and noBall 1 increment
    */

//        public void simulateInnings(InningsUtil inningsUtil) throws InterruptedException, IOException {
//        sendMessage("Innings Start from the test simulation: " + inningsUtil.getBattingTeam().getName());
//
//        List<PlayerUtil> battingPlayerUtils = inningsUtil.getBattingTeam().getPlayers();
//        List<PlayerObject> batters = convertToPlayerObjects(battingPlayerUtils);
//
//        List<PlayerUtil> bowlingPlayerUtils = inningsUtil.getBowlingTeam().getPlayers();
//        List<PlayerObject> bowlers = convertToPlayerObjects(bowlingPlayerUtils);
//
//        Queue<PlayerObject> availableBatters = new LinkedList<>(batters);
//        Queue<PlayerObject> availableBowlers = new LinkedList<>(bowlers);
//
//        PlayerObject striker = availableBatters.poll();
//        PlayerObject nonStriker = availableBatters.poll();
//        PlayerObject bowler = availableBowlers.poll();
//
//        if (striker == null || nonStriker == null || bowler == null) {
//            System.err.println("Error: Striker, Non-striker, or Bowler is null.");
//            return;
//        }
//
//        int bowlerIndex = 0;
//        int totalOvers = inningsUtil.getTotalOvers();
//        int ballsPlayed = 0;
//        int oversCompleted = 0;
//        int ballsInCurrentOver = 0;
//        int consecutiveBouncers = 0;
//
//        while (inningsUtil.getWickets() < 10 && inningsUtil.getRuns() < 200 && ballsPlayed < totalOvers * 6) {
//            if (ballsInCurrentOver == 6) {
//                // End of over logic
//                bowler.addOverBowled();
//                oversCompleted++;
//                ballsInCurrentOver = 0;
//                consecutiveBouncers = 0; // Reset bouncer count
//                sendMessage("Over " + oversCompleted + " End");
//
//                // Rotate bowlers
//                bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
//                bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
//            }
//
//            // Process each ball, potentially multiple times if it’s a no-ball or wide
//            boolean ballProcessed = false;
//            while (!ballProcessed) {
//                sendMessage("Over " + (oversCompleted + 1) + " Ball " + (ballsInCurrentOver + 1));
//                assert bowler != null;
//                sendMessage("Bowler: " + bowler.getPlayer().getName());
//                assert striker != null;
//                sendMessage("Striker: " + striker.getPlayer().getName());
//                assert nonStriker != null;
//                sendMessage("Non-Striker: " + nonStriker.getPlayer().getName());
//
//                BallOutcomeUtil outcome = simulateBallEvent(inningsUtil, striker, nonStriker, bowler);
//
//                // Print ball details
//                String ballDetail = "Ball Details: Runs Scored: " + outcome.getRuns();
//                if (outcome.isWicket()) {
//                    ballDetail += ", Wicket: " + outcome.getWicketType() + " by " + outcome.getBowler().getPlayer().getName();
//                }
//                if (outcome.getBallType() == BallTypeUtil.NO_BALL) {
//                    ballDetail += ", No Ball";
//                    inningsUtil.incrementBall(); // No-ball does not count towards the over
//                    // Re-bowl the no-ball
//                    continue;
//                } else if (outcome.getBallType() == BallTypeUtil.WIDE) {
//                    ballDetail += ", Wide";
//                    inningsUtil.incrementBall(); // Wide does not count towards the over
//                    // Re-bowl the wide
//                    continue;
//                } else if (outcome.getBallType() == BallTypeUtil.BOUNCER) {
//                    ballDetail += ", Bouncer";
//                    consecutiveBouncers++;
//                    if (consecutiveBouncers >= 3) {
//                        inningsUtil.incrementBall(); // Increment ball count for continuous bouncers
//                        consecutiveBouncers = 0; // Reset bouncer count
//                    }
//                } else {
//                    consecutiveBouncers = 0; // Reset bouncer count for other deliveries
//                }
//                sendMessage(ballDetail);
//
//                // Update players based on ball outcome
//                if (outcome.isWicket()) {
//                    inningsUtil.incrementWickets();
//                    sendMessage("Wicket " + striker.getPlayer().getName() + " is out.");
//                    if (!availableBatters.isEmpty()) {
//                        striker = availableBatters.poll();  // Replace striker
//                    } else {
//                        striker = null;  // All batters are out
//                    }
//                } else {
//                    inningsUtil.addRuns(outcome.getRuns()); // Add runs to the innings
//                    striker.addScore(outcome.getRuns()); // Update striker's score
//                    striker.addBallFaced(); // Increment balls faced for striker
//                    if (outcome.getRuns() == 0) {
//                        striker.addDotBall(); // Add dot ball to striker
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
//                TimeUnit.SECONDS.sleep(getDelayBasedOnEvent(inningsUtil.getRuns(), inningsUtil.getWickets()));
//                ballProcessed = true; // Exit the ball processing loop
//            }
//
//            // Increment ball count for valid deliveries
//            if (ballsInCurrentOver < 6) {
//                ballsInCurrentOver++;
//            }
//            ballsPlayed++;
//        }
//
//        if (ballsInCurrentOver > 0) {
//            bowler.addOverBowled();
//            oversCompleted++;
//        }
//        sendMessage("Innings End: " + inningsUtil.getBattingTeam().getName() + "  " + inningsUtil.getRuns() + "/" + inningsUtil.getWickets() + " in " + oversCompleted + "." + ballsInCurrentOver);
//
//        sendMessage("Player Stats:");
//        for (PlayerObject player : batters) {
//            sendMessage(player.getPlayer().getName() + ": " +
//                    "Runs = " + player.getScore() +
//                    ", Balls Faced = " + player.getBallsFaced() +
//                    ", Fours = " + player.getFours() +
//                    ", Sixes = " + player.getSixes() +
//                    ", Dot Balls = " + player.getDotBalls() +
//                    ", Singles = " + player.getSingles() +
//                    ", Twos = " + player.getTwos() +
//                    ", Threes = " + player.getThrees());
//        }
//
//        sendMessage("Bowler Stats:");
//        for (PlayerObject currBowler : bowlers) {
//            sendMessage(currBowler.getPlayer().getName() + ": " +
//                    "Overs Bowled = " + currBowler.getOversBowled() +
//                    ", Wickets Taken = " + currBowler.getWicketsTaken());
//        }
//    }


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
                return 1; // Longer delay for a wicket
            } else if (runs % 6 == 0) {
                return 1; // Delay for a six
            } else {
                return 1; // Normal delay
            }
        }

//        private static BallOutcomeUtil simulateBallEvent(InningsUtil inningsUtil, PlayerObject striker, PlayerObject nonStriker, PlayerObject bowler) {
//            Random rand = new Random();
//            BallTypeUtil ballTypeUtil = BallTypeUtil.NORMAL;
//
//            // Randomly determine ball type
//            int ballTypeIndex = rand.nextInt(BallTypeUtil.values().length);
//            ballTypeUtil = BallTypeUtil.values()[ballTypeIndex];
//
//            int runs = 0;
//            boolean isWicket = false;
//
//            // Handle ball types
//            switch (ballTypeUtil) {
//                case NO_BALL:
//                    isWicket = false; // Wicket can't be taken on a no-ball
//                    runs = 1; // No-ball gives 1 extra run
//                    break;
//                case WIDE:
//                    isWicket = false; // Wicket can't be taken on a wide
//                    runs = 1; // Wide gives 1 extra run
//                    break;
//                case BOUNCER:
//                    runs = rand.nextInt(7); // Bouncer can have runs from 0 to 6
//                    break;
//                case NORMAL:
//                    runs = rand.nextInt(7); // Normal ball can have runs from 0 to 6
//                    break;
//            }
//
//            if (ballTypeUtil != BallTypeUtil.NO_BALL && rand.nextInt(10) < 2) { // 20% chance of a wicket for normal balls
//                isWicket = true;
//                int wicketTypeIndex = rand.nextInt(WicketTypeUtil.values().length);
//                WicketTypeUtil wicketTypeUtil = WicketTypeUtil.values()[wicketTypeIndex];
//                bowler.addWicket();
//                return new BallOutcomeUtil(runs, true, wicketTypeUtil, bowler, ballTypeUtil);
//            } else {
//                inningsUtil.addRuns(runs);
//                if (runs == 0 && ballTypeUtil == BallTypeUtil.NORMAL) {
//                    striker.addDotBall(); // Add dot ball to striker
//                }
//                striker.addScore(runs); // Add runs to striker
//                striker.addBallFaced(); // Increment balls faced
//                return new BallOutcomeUtil(runs, false, null, null, ballTypeUtil);
//            }
//        }


    /*
    * this ball outcome is changed before everything is working fine okay
    * */

    private static int consecutiveBouncers = 0;

    private static BallOutcomeUtil simulateBallEvent(InningsUtil inningsUtil, PlayerObject striker, PlayerObject nonStriker, PlayerObject bowler) {
        Random rand = new Random();
        BallTypeUtil ballTypeUtil = BallTypeUtil.NORMAL;

        // Determine ball type with adjusted probabilities
        int ballTypeIndex = rand.nextInt(12); // Increased range for reduced probability of no-ball
        if (ballTypeIndex < 2) { // 16.67% chance for no-ball
            ballTypeUtil = BallTypeUtil.NO_BALL;
        } else if (ballTypeIndex < 4) { // 16.67% chance for wide
            ballTypeUtil = BallTypeUtil.WIDE;
        } else if (ballTypeIndex < 8) { // 33.33% chance for bouncer
            ballTypeUtil = BallTypeUtil.BOUNCER;
        } else { // Remaining 33.33% for normal
            ballTypeUtil = BallTypeUtil.NORMAL;
        }

        int runs = 0;
        boolean isWicket = false;

        switch (ballTypeUtil) {
            case NO_BALL:
                // Runs can be between 1 and 6
                runs = rand.nextInt(6) + 1;
                isWicket = false; // Wicket cannot occur on a no-ball
                // Increment ball count for no-ball
                inningsUtil.incrementBall();
                break;
            case WIDE:
                runs = 1; // A wide always gives 1 run
                isWicket = false; // Wicket cannot occur on a wide
                // Increment ball count for wide
                inningsUtil.incrementBall();
                break;
            case BOUNCER:
                runs = rand.nextInt(7); // Runs from 0 to 6
                consecutiveBouncers++;
                // Check for 3 consecutive bouncers
                if (consecutiveBouncers >= 3) {
                    // Increment ball count and reset consecutive bouncers
                    inningsUtil.incrementBall();
                    consecutiveBouncers = 0;
                }
                break;
            case NORMAL:
                runs = rand.nextInt(7); // Runs from 0 to 6
                consecutiveBouncers = 0; // Reset consecutive bouncers on normal delivery
                if (rand.nextInt(10) < 2) { // 20% chance of a wicket for normal balls
                    isWicket = true;
                }
                break;
        }

        if (isWicket) {
            bowler.addWicket();
            return new BallOutcomeUtil(runs, true, determineWicketType(), bowler, ballTypeUtil);
        } else {
            inningsUtil.addRuns(runs);
            striker.addScore(runs);
            striker.addBallFaced();
            if (runs == 0 && ballTypeUtil == BallTypeUtil.NORMAL) {
                striker.addDotBall(); // Add dot ball to striker
            }

            // Increment ball count for legal deliveries (not wides)
            if (ballTypeUtil != BallTypeUtil.WIDE) {
                inningsUtil.incrementBall();
            }

            return new BallOutcomeUtil(runs, false, null, null, ballTypeUtil);
        }
    }

        // Helper method to determine the type of wicket
        private static WicketTypeUtil determineWicketType() {
            Random rand = new Random();
            int wicketTypeIndex = rand.nextInt(WicketTypeUtil.values().length);
            return WicketTypeUtil.values()[wicketTypeIndex];
        }


//         void printMatchResult(InningsUtil inningsUtilA, InningsUtil inningsUtilB) throws IOException {
//            int scoreA = inningsUtilA.getRuns();
//            int scoreB = inningsUtilB.getRuns();
//
//            String result;
//            if (scoreA > scoreB) {
//                result = inningsUtilA.getBattingTeam().getName() + " won by " + (scoreA - scoreB) + " runs";
//            } else if (scoreB > scoreA) {
//                result = inningsUtilB.getBattingTeam().getName() + " won by " + (10 - inningsUtilB.getWickets()) + " wickets";
//            } else {
//                result = "Match tied";
//            }
//
//            sendMessage("Match Result: " + result);
//        }

    public void printMatchResult(InningsUtil inningsUtilA, InningsUtil inningsUtilB) throws IOException {
        int scoreA = inningsUtilA.getRuns();
        int scoreB = inningsUtilB.getRuns();
        String result;
        int pointsA = 0;
        int pointsB = 0;

        if (scoreA > scoreB) {
            result = inningsUtilA.getBattingTeam().getName() + " won by " + (scoreA - scoreB) + " runs";
            pointsA = 2;
            pointsB = 0;
        } else if (scoreB > scoreA) {
            result = inningsUtilB.getBattingTeam().getName() + " won by " + (10 - inningsUtilA.getWickets()) + " wickets";
            pointsA = 0;
            pointsB = 2;
        } else {
            result = "Match tied";
            pointsA = 1;
            pointsB = 1;
        }

        // Update team stats with final result
        updateFinalTeamStats(inningsUtilA, result, pointsA);
        updateFinalTeamStats(inningsUtilB, result, pointsB);

        sendMessage("Match Result: " + result);
    }


    private void updateFinalTeamStats(InningsUtil inningsUtil, String result, int points) {
//        TeamStats teamStats = teamStatsService.getTeamStatsByMatchIdAndTeamName(inningsUtil.getMatchId(), inningsUtil.getBattingTeam().getName());
        TeamStats teamStats = teamStatsRepository.findTeamStatsByMatchIdAndTeamName(inningsUtil.getMatch().getMatchId(), inningsUtil.getBattingTeam().getName());
        if (teamStats.getNumberOfWins() == null) {
            teamStats.setNumberOfWins(0);
        }
        if (teamStats.getNumberOfLosses() == null) {
            teamStats.setNumberOfLosses(0);
        }
        if(points == 2 ){
            teamStats.setNumberOfWins(teamStats.getNumberOfWins() + 1);
        } else if (points == 0) {
            teamStats.setNumberOfLosses(teamStats.getNumberOfLosses() + 1);
        }
        teamStats.setResult(result);
            teamStats.setPoints(points);
            teamStats.setMatchesPlayed(teamStats.getMatchesPlayed() + 1);// Increment if necessary
            teamStats.setStatus("completed");
            teamStatsRepository.save(teamStats);

    }

}


