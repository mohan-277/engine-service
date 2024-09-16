package com.sbear.gameengineservice.websocket.services;
import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.*;
import com.sbear.gameengineservice.entity.constants.BallType;
import com.sbear.gameengineservice.entity.constants.MatchConstants;
import com.sbear.gameengineservice.entity.constants.WicketType;
import com.sbear.gameengineservice.entity.stats.PlayerStats;
import com.sbear.gameengineservice.entity.stats.StatusOfMatch;
import com.sbear.gameengineservice.entity.stats.TeamStats;
import com.sbear.gameengineservice.repository.*;
import com.sbear.gameengineservice.repository.stats.PlayerStatsRepository;
import com.sbear.gameengineservice.repository.stats.TeamStatsRepository;
import com.sbear.gameengineservice.utilities.*;
import com.sbear.gameengineservice.utilities.PlayerObject;
import com.sbear.gameengineservice.websocket.WebSocketSessionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CricketSimulation {

        @Autowired
        private TeamRepository teamRepository;
        @Autowired
        private PlayerRepository playerRepository;
        
        @Autowired
        private PlayerStatsRepository playerStatsRepository;
        
        @Autowired
        private TeamStatsRepository teamStatsRepository;


        @Autowired
        private StatusOfMatchRepository statusOfMatchRepository;


        @Autowired
        private WebSocketSessionManager webSocketSessionManager; // WebSocket session reference

        @Autowired
        private CricketMatchRepository cricketMatchRepository;
        @Autowired
        private InningsRepository inningsRepository;
        @Autowired
        private BallRepository ballRepository;


    private void sendMessage(String message) {
            webSocketSessionManager.sendMessage(message);
        }

//       @Transactional
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
                    matchDTO.getMatchType(),
                    matchDTO.getMatchId(),
                    matchDTO.getMatchDateTime()
            );
        }

//        @Transactional
        public void simulateMatchFromDTO(MatchDetailsDTO matchDTO) throws InterruptedException, IOException {


            Optional<CricketMatch> optionalMatch = cricketMatchRepository.findById(matchDTO.getMatchId());
            if (optionalMatch.isPresent()) {
                CricketMatch cricketMatch = optionalMatch.get();
                cricketMatch.setLive(true);
                cricketMatchRepository.save(cricketMatch);
            }

            // this is for initial creating the teamStats
            createOrUpdateTeamStats(matchDTO,matchDTO.getTeamA());
            createOrUpdateTeamStats(matchDTO,matchDTO.getTeamB());




            // this check for how many times each match is starts with the stage // group stages, knock out, finals
            Long newCount = getNextCountForMatchStage(matchDTO.getMatchStage());

            // Create a new StatusOfMatch instance
            StatusOfMatch statusOfMatch = statusOfMatchRepository.findTopByMatchStageNameOrderByCountDesc(matchDTO.getMatchStage());
            if(statusOfMatch == null) {
                statusOfMatch = new StatusOfMatch();
                statusOfMatch.setMatchId(matchDTO.getMatchId());
                statusOfMatch.setCount(newCount);
                statusOfMatchRepository.save(statusOfMatch);
            }

            statusOfMatch.setMatchStageName(matchDTO.getMatchStage());
            statusOfMatch.setMatchId(matchDTO.getMatchId());
            statusOfMatch.setCount(newCount);
            statusOfMatchRepository.save(statusOfMatch);

            // will simulate the match
            simulateMatch(convertDTOToCricketMatchUtil(matchDTO));
        }


    public Long getNextCountForMatchStage(String matchStageName) {
        StatusOfMatch latestStatus = statusOfMatchRepository.findTopByMatchStageNameOrderByCountDesc(matchStageName);
        return (latestStatus == null) ? 1L : latestStatus.getCount() + 1;
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


            CricketMatch cricketMatch = cricketMatchRepository.findById(match.getMatchId()).get();
            cricketMatch.setLive(true);

            cricketMatchRepository.save(cricketMatch);

            // Check if innings already exist for this match
            List<Innings> existingInnings = inningsRepository.findByCricketMatchId(match.getMatchId());

            if (!existingInnings.isEmpty()) {
                // If innings already exist, send a message and return
                sendMessage("Innings for match ID " + match.getMatchId() + " are already completed.");
                return;
            }

            // Simulate the innings
            InningsUtil inningsUtilA = new InningsUtil(match, match.getTeamA(), match.getTeamB(),1L);
            InningsUtil inningsUtilB = new InningsUtil(match, match.getTeamB(), match.getTeamA(),2L);
            Innings  inningsA = new Innings();
            inningsA.setInningsNumber(1L);
            inningsA.setMatch(getCricketMatchFormUtil(inningsUtilA.getMatch().getMatchId()));
            inningsA.setBattingTeam(getTeamBy(inningsUtilA.getBattingTeam().getName()));
            inningsA.setBowlingTeam(getTeamBy(inningsUtilA.getBowlingTeam().getName()));
            inningsA.setIsCompleted(false);
            inningsA.setTotalOvers(0);
            inningsRepository.save(inningsA);

            Innings inningsB = new Innings();
            inningsB.setInningsNumber(2L);
            inningsB.setMatch(getCricketMatchFormUtil(match.getMatchId()));
            inningsB.setBattingTeam(getTeamBy(inningsUtilB.getBattingTeam().getName()));
            inningsB.setBowlingTeam(getTeamBy(inningsUtilB.getBowlingTeam().getName()));
            inningsB.setIsCompleted(false);
            inningsB.setTotalOvers(0);
            inningsRepository.save(inningsB);


            Thread.sleep(TimeUnit.SECONDS.toMillis(2));

            simulateInnings(inningsUtilA, match.getMatchId());// first innings

            Thread.sleep(TimeUnit.SECONDS.toMillis(2));

            simulateInnings(inningsUtilB, match.getMatchId()); // second innings

            Thread.sleep(TimeUnit.SECONDS.toMillis(2));


            cricketMatch.setMatchStatus("Completed");  // at the end it is necessary to complete
            cricketMatchRepository.save(cricketMatch);

            printMatchResult(inningsUtilA, inningsUtilB);
        }

        private CricketMatch getCricketMatchFormUtil(Long matchId) {
          Optional<CricketMatch> optionalMatch = cricketMatchRepository.findById(matchId);
          return optionalMatch.orElse(null);
        }

        private Team getTeamBy(String teamName) {
           Optional<Team> teamOptional = teamRepository.findByName(teamName);
           return teamOptional.orElse(null);
        }


        public void simulateInnings(InningsUtil inningsUtil, Long matchId) throws InterruptedException, IOException {
            sendMessage("batting Team : " + inningsUtil.getBattingTeam().getName());
            sendMessage("bowling Team: " + inningsUtil.getBowlingTeam().getName());

            List<PlayerUtil> battingPlayerUtils = inningsUtil.getBattingTeam().getPlayers();
            List<PlayerObject> batters = convertToPlayerObjects(battingPlayerUtils);

            List<PlayerUtil> bowlingPlayerUtils = inningsUtil.getBowlingTeam().getPlayers();
            List<PlayerObject> bowlers = convertToPlayerObjects(bowlingPlayerUtils);

            List<PlayerObject> battingPlayers = getBatters(battingPlayerUtils);
            List<PlayerObject> bowlingPlayers = getBowlers(bowlingPlayerUtils);

            Queue<PlayerObject> availableBatters = new LinkedList<>(battingPlayers);
            Queue<PlayerObject> availableBowlers = new LinkedList<>(bowlingPlayers);

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

                saveBallDetails(matchId, inningsUtil.getInningsId(), striker, nonStriker, bowler, oversCompleted, ballsInCurrentOver, outcome);


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

//            sendMessage("Player Stats:");
//            for (PlayerObject player : batters) {
//                sendMessage(player.getPlayer().getName() + ": " +
//                        "Runs = " + player.getScore() +
//                        ", Balls Faced = " + player.getBallsFaced() +
//                        ", Fours = " + player.getFours() +
//                        ", Sixes = " + player.getSixes() +
//                        ", Dot Balls = " + player.getDotBalls() +
//                        ", Singles = " + player.getSingles() +
//                        ", Twos = " + player.getTwos() +
//                        ", Threes = " + player.getThrees());
//            }
//
//            for (PlayerObject currBatter : batters) {
//                PlayerStats newStats = getBatterStats(inningsUtil, currBatter,matchId);
////                PlayerStats existingStats = playerStatsRepository.findByCurrentPlayingMatchIdAndPlayerName(matchId, currBatter.getPlayer().getName());
//
////                if (existingStats != null) {
////                    // Update existing record
////                    existingStats.setRuns(newStats.getRuns());
////                    existingStats.setBallsFaced(newStats.getBallsFaced());
////                    existingStats.setFours(newStats.getFours());
////                    existingStats.setSixes(newStats.getSixes());
////                    existingStats.setDotBalls(newStats.getDotBalls());
////                    existingStats.setSingles(newStats.getSingles());
////                    existingStats.setTwos(newStats.getTwos());
////                    existingStats.setThrees(newStats.getThrees());
////                    // Other fields you want to update
////                    playerStatsRepository.save(existingStats);
////                } else {
////                    // Save new record
////                    playerStatsRepository.save(newStats);
////                }
//                playerStatsRepository.save(newStats);
//            }
//
//            sendMessage("Bowler Stats:");
//            for (PlayerObject currBowler : bowlers) {
//                sendMessage(currBowler.getPlayer().getName() + ": " +
//                        "Overs Bowled = " + currBowler.getOversBowled() +
//                        ", Wickets Taken = " + currBowler.getWicketsTaken());
//            }
//
//            for (PlayerObject currBowler : bowlers) {
//                PlayerStats bowlerStats = getBowlerStats(inningsUtil, currBowler,matchId);
//                playerStatsRepository.save(bowlerStats);
////                PlayerStats newStats = getBowlerStats(inningsUtil, currBowler,matchId);
////                PlayerStats existingStats = playerStatsRepository.findByCurrentPlayingMatchIdAndPlayerName(matchId, currBowler.getPlayer().getName());
////
////                if (existingStats != null) {
////                    // Update existing record
////                    existingStats.setRuns(newStats.getRuns());
////                    existingStats.setBallsFaced(newStats.getBallsFaced());
////                    existingStats.setFours(newStats.getFours());
////                    existingStats.setSixes(newStats.getSixes());
////                    existingStats.setDotBalls(newStats.getDotBalls());
////                    existingStats.setSingles(newStats.getSingles());
////                    existingStats.setTwos(newStats.getTwos());
////                    existingStats.setThrees(newStats.getThrees());
////                    // Other fields you want to update
////                    playerStatsRepository.save(existingStats);
////                } else {
////                    // Save new record
////                    playerStatsRepository.save(newStats);
////                }
//            }

            List<PlayerStats> existingStats = playerStatsRepository.findByCurrentPlayingMatchId(matchId);
            Map<String, PlayerStats> existingStatsMap = existingStats.stream()
                    .collect(Collectors.toMap(PlayerStats::getPlayerName, ps -> ps));

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
                Thread.sleep(2);
                PlayerStats newStats = getBatterStats(inningsUtil, player, matchId);
                PlayerStats existingStatsForPlayer = existingStatsMap.get(player.getPlayer().getName());

                if (existingStatsForPlayer != null) {
                    // Update existing record
                    existingStatsForPlayer.setRuns(newStats.getRuns());
                    existingStatsForPlayer.setBallsFaced(newStats.getBallsFaced());
                    existingStatsForPlayer.setFours(newStats.getFours());
                    existingStatsForPlayer.setSixes(newStats.getSixes());
                    existingStatsForPlayer.setDotBalls(newStats.getDotBalls());
                    existingStatsForPlayer.setSingles(newStats.getSingles());
                    existingStatsForPlayer.setTwos(newStats.getTwos());
                    existingStatsForPlayer.setThrees(newStats.getThrees());
                    playerStatsRepository.save(existingStatsForPlayer);
                } else {
                    // Save new record
                    playerStatsRepository.save(newStats);
                }
            }

            sendMessage("Bowler Stats:");
            for (PlayerObject currBowler : bowlers) {
                sendMessage(currBowler.getPlayer().getName() + ": " +
                        "Overs Bowled = " + currBowler.getOversBowled() +
                        ", Wickets Taken = " + currBowler.getWicketsTaken());

                PlayerStats bowlerStats = getBowlerStats(inningsUtil, currBowler, matchId);
                PlayerStats existingBowlerStats = existingStatsMap.get(currBowler.getPlayer().getName());

                if (existingBowlerStats != null) {
                    // Update existing record
                    existingBowlerStats.setOversBowled(bowlerStats.getOversBowled());
                    existingBowlerStats.setWicketsTaken(bowlerStats.getWicketsTaken());
                    playerStatsRepository.save(existingBowlerStats);
                } else {
                    // Save new record
                    playerStatsRepository.save(bowlerStats);
                }
            }



        }


//    @Transactional
    public void saveBallDetails(Long matchId, Long inningsId, PlayerObject striker, PlayerObject nonStriker, PlayerObject bowler, int oversCompleted, int ballsInCurrentOver, BallOutcomeUtil outcome) {

        // Retrieve or create innings
        Innings innings = inningsRepository.findByInningsNumberAndMatchId(inningsId, matchId)
                .orElseGet(() -> {
                    Innings newInnings = new Innings();
                    newInnings.setInningsNumber(inningsId);
                    newInnings.setMatch(cricketMatchRepository.findById(matchId)
                            .orElseThrow(() -> new RuntimeException("Match not found for matchId: " + matchId)));
                    newInnings.setTotalScore(0);  // Initialize total score
                    newInnings.setWicketCount(0); // Initialize wicket count
                    newInnings.setBalls(new ArrayList<>()); // Initialize the list of balls
                    return newInnings;
                });

        // Create and populate the Ball entity
        Ball ballDetails = new Ball();
        ballDetails.setMatchId(matchId);
        ballDetails.setInnings(innings);
        ballDetails.setStrikerName(striker.getPlayer().getName());
        ballDetails.setNonStrikerName(nonStriker.getPlayer().getName());
        ballDetails.setBowlerName(bowler.getPlayer().getName());
        ballDetails.setOverNumber(oversCompleted + 1); // assuming overs start from 0
        ballDetails.setBallNumber(ballsInCurrentOver);
        ballDetails.setRunsScored(outcome.getRuns() != null ? outcome.getRuns() : 0);

        // Handle wicket type
        ballDetails.setWicketType(outcome.getWicketType() != null ? WicketType.valueOf(outcome.getWicketType().name()) : WicketType.NONE);
        ballDetails.setBallType(BallType.valueOf(outcome.getBallType().name()));

        int runsScored = outcome.getRuns() != null ? outcome.getRuns() : 0;
        int currentTotalScore = innings.getTotalScore() != null ? innings.getTotalScore() : 0;
        int newTotalScore = currentTotalScore + runsScored;

        // Set totalScore and wicketCount for the ballDetails
        ballDetails.setTotalScore(newTotalScore);
        // Determine the updated wicket count
        int newWicketCount = innings.getWicketCount() != null ? innings.getWicketCount() : 0;
        if (ballDetails.getWicketType() != WicketType.NONE) {
            newWicketCount++;
            bowler.incrementWickets();
        }
        ballDetails.setWicketCount(newWicketCount);

        bowler.incrementBallsBowled();
        ballDetails.setBallsBowled(bowler.getBallsBowled());
        ballDetails.setWicketsTaken(bowler.getWicketsTaken());

        // Save the ball details first
        innings.setBalls(ballDetails.getInnings().getBalls());
        inningsRepository.save(innings);

        ballRepository.save(ballDetails);


        // Update innings record after saving the ball
        innings.setTotalScore(innings.getTotalScore() != null ? innings.getTotalScore()+runsScored : 0);
        innings.setWicketCount(newWicketCount);
//        innings.getBalls().add(ballDetails);

        // Save the updated innings record
        inningsRepository.save(innings);
    }



    private static PlayerStats getBowlerStats(InningsUtil inningsUtil, PlayerObject currBowler,Long matchId) {
        PlayerStats bowlerStats = new PlayerStats();
        bowlerStats.setPlayerName(currBowler.getPlayer().getName());
        bowlerStats.setTeamName(inningsUtil.getBowlingTeam().getName());
        bowlerStats.setPlayerType(currBowler.getPlayer().getSpecialization());
        bowlerStats.setRuns(0);
        bowlerStats.setBallsFaced(0);
        bowlerStats.setFours(0);
        bowlerStats.setSixes(0);
        bowlerStats.setDotBalls(0);
        bowlerStats.setSingles(0);
        bowlerStats.setTwos(0);
        bowlerStats.setThrees(0);
        bowlerStats.setOversBowled(currBowler.getOversBowled());
        bowlerStats.setWicketsTaken(currBowler.getWicketsTaken());
        bowlerStats.setCurrentPlayingMatchId(matchId);
        return bowlerStats;
    }

    private static PlayerStats getBatterStats(InningsUtil inningsUtil, PlayerObject player, Long matchId) {
        PlayerStats playerStats = new PlayerStats();
        playerStats.setPlayerName(player.getPlayer().getName());
        playerStats.setTeamName(inningsUtil.getBattingTeam().getName());
        playerStats.setPlayerType(player.getPlayer().getSpecialization());
        playerStats.setRuns(player.getScore());
        playerStats.setBallsFaced(player.getBallsFaced());
        playerStats.setFours(player.getFours());
        playerStats.setSixes(player.getSixes());
        playerStats.setDotBalls(player.getDotBalls());
        playerStats.setSingles(player.getSingles());
        playerStats.setTwos(player.getTwos());
        playerStats.setThrees(player.getThrees());
        playerStats.setCurrentPlayingMatchId(matchId);
        playerStats.setOversBowled(0);
        playerStats.setWicketsTaken(0);
        return playerStats;
    }



    public List<PlayerObject> convertToPlayerObjects(List<PlayerUtil> playerUtils) {
        List<PlayerObject> playerObjects = new ArrayList<>();
        for (PlayerUtil playerUtil : playerUtils) {
            playerObjects.add(new PlayerObject(playerUtil));
        }
        return playerObjects;
    }
    private List<PlayerObject> getBatters(List<PlayerUtil> allPlayers) {
        return allPlayers.stream()
                .filter(player -> "Batter".equalsIgnoreCase(player.getSpecialization()) ||
                        "All-Rounder".equalsIgnoreCase(player.getSpecialization()))
                .map(PlayerObject::new) // Convert each PlayerUtil to PlayerObject
                .collect(Collectors.toList());
    }

    // Get a list of bowlers and all-rounders
    private List<PlayerObject> getBowlers(List<PlayerUtil> allPlayers) {
        return allPlayers.stream()
                .filter(player -> "Bowler".equalsIgnoreCase(player.getSpecialization()) ||
                        "All-Rounder".equalsIgnoreCase(player.getSpecialization()))
                .map(PlayerObject::new)
                .collect(Collectors.toList());
    }


    private static PlayerObject getRandomPlayer(List<PlayerObject> players) {
            Random random = new Random();
            return players.get(random.nextInt(players.size()));
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



    private static int consecutiveBouncers = 0;

    private static BallOutcomeUtil simulateBallEvent(InningsUtil inningsUtil, PlayerObject striker, PlayerObject nonStriker, PlayerObject bowler) {
        Random rand = new Random();
        BallTypeUtil ballTypeUtil;

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

            if (ballTypeUtil != BallTypeUtil.WIDE) {
                inningsUtil.incrementBall();
            }

            return new BallOutcomeUtil(runs, false, null, null, ballTypeUtil);
        }
    }

    private static WicketTypeUtil determineWicketType() {
        Random rand = new Random();
        int wicketTypeIndex = rand.nextInt(WicketTypeUtil.values().length);
        return WicketTypeUtil.values()[wicketTypeIndex];
    }



    public void printMatchResult(InningsUtil inningsUtilA, InningsUtil inningsUtilB)  {
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

        // Update team stats with a final result
        updateFinalTeamStats(inningsUtilA, result, pointsA);
        updateFinalTeamStats(inningsUtilB, result, pointsB);

        sendMessage("Match Result: " + result);
    }


    private void updateFinalTeamStats(InningsUtil inningsUtil, String result, int points) {
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
            teamStats.setStatus(MatchConstants.COMPLETED);
            teamStatsRepository.save(teamStats);
    }

}


