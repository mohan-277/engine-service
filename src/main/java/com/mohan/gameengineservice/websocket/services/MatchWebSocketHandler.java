package com.mohan.gameengineservice.websocket.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohan.gameengineservice.dto.MatchDetailsDTO;
import com.mohan.gameengineservice.entity.Player;
import com.mohan.gameengineservice.entity.Team;
import com.mohan.gameengineservice.repository.PlayerRepository;
import com.mohan.gameengineservice.repository.TeamRepository;
import com.mohan.gameengineservice.utilities.*;
import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import static com.mohan.gameengineservice.websocket.services.CricketMatchV3Util.convertToPlayerObjects;
import static com.mohan.gameengineservice.websocket.services.CricketMatchV3Util.printMatchResult;

@Component
public class MatchWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private PlayerRepository playerRepository;


    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("WebSocket connection established with " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("WebSocket connection closed with " + session.getId());
    }

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


    @Transactional
    @Override
    protected void handleTextMessage(@Nonnull WebSocketSession session, TextMessage message) throws IOException, InterruptedException {
        String payload = message.getPayload();
        System.out.println(payload);
        // If the message contains "START_MATCH", simulate the match with the provided DTO
        if ("START_MATCH".equalsIgnoreCase(payload)) {
            // Sample DTO, in practice this should be extracted from a message or pre-defined

//            MatchDetailsDTO matchDTO = objectMapper.readValue(payload, MatchDetailsDTO.class);
            MatchDetailsDTO matchDTO = new MatchDetailsDTO();
            matchDTO.setMatchId(19L);
            matchDTO.setTeamA("Team Australia");
            matchDTO.setTeamB("Team South Africa");
            matchDTO.setMatchDateTime(LocalDateTime.parse("2024-10-02T12:00:00"));
            matchDTO.setLocation("India - Eden Gardens");
            matchDTO.setMatchType("Smartbear tournament");
            matchDTO.setMatchStage("PLAYOFF");
            matchDTO.setMatchGroup("Group B");
            matchDTO.setLive(false);
            // Simulate match with DTO
            simulateMatchFromDTO(matchDTO);
            sendMatchUpdate("Match has started with ID: " + matchDTO.getMatchId());

        } else {
            // Deserialize payload to MatchDetailsDTO

            // Simulate match with received DTO

        }
    }

    public void sendMatchUpdate(String update) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(update));
                } catch (IOException e) {
                    e.printStackTrace(); // Handle the exception appropriately
                }
            }
        }
    }


//    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
//
//    @Override
//    public void afterConnectionEstablished(@Nonnull  WebSocketSession session) {
//        sessions.add(session);
//    }
//
//    @Override
//    public void afterConnectionClosed(@Nonnull  WebSocketSession session, @Nonnull CloseStatus status) {
//        sessions.remove(session);
//    }
//
//    public void sendMatchUpdate(MatchDto matchDto) {
//        String message = convertToJson(matchDto); // Serialize MatchDto to JSON
//        synchronized (sessions) {
//            for (WebSocketSession session : sessions) {
//                if (session.isOpen()) {
//                    try {
//                        session.sendMessage(new TextMessage(message));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//    private String convertToJson(MatchDto matchDto) {
//        // Convert MatchDto to JSON string. You can use libraries like Jackson or Gson.
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            return objectMapper.writeValueAsString(matchDto);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            return "{}";
//        }
//    }


    private  void simulateMatch(CricketMatchUtil match) throws InterruptedException {
        String message = "Match Started from the matchWebsocketHandler: " + match.getTeamA().getName() + " vs " + match.getTeamB().getName();

        System.out.println(message);

        // Simulate the innings
        InningsUtil inningsUtilA = new InningsUtil(match, match.getTeamA(), match.getTeamB());
        InningsUtil inningsUtilB = new InningsUtil(match, match.getTeamB(), match.getTeamA());

        simulateInnings(inningsUtilA);
        simulateInnings(inningsUtilB);

        // Print match result
        printMatchResult(inningsUtilA, inningsUtilB);
    }

    private  void simulateInnings(InningsUtil inningsUtil) throws InterruptedException {
        String message = "Innings Start: " + inningsUtil.getBattingTeam().getName();
        System.out.println(message);

        List<PlayerUtil> battingPlayerUtils = inningsUtil.getBattingTeam().getPlayers();
        List<PlayerObject> batters = convertToPlayerObjects(battingPlayerUtils);
        System.out.println(batters.size() + " ");
        System.out.println(batters.get(0).getPlayer().getName() + " batter player Name");

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
                message = "Over " + oversCompleted + " End";
                sendMatchUpdate(message);
                System.out.println(message);

                // Rotate bowlers
                bowlerIndex = (bowlerIndex + 1) % availableBowlers.size();
                bowler = availableBowlers.toArray(new PlayerObject[0])[bowlerIndex];
            }

            ballsPlayed++;
            ballsInCurrentOver++;

            message = "Over " + (oversCompleted + 1) + " Ball " + ballsInCurrentOver;
           sendMatchUpdate(message);
            assert bowler != null;
            sendMatchUpdate("Bowler: " + bowler.getPlayer().getName());
            assert striker != null;
            sendMatchUpdate("Striker: " + striker.getPlayer().getName());
            assert nonStriker != null;
            sendMatchUpdate("Non-Striker: " + nonStriker.getPlayer().getName());

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
            sendMatchUpdate(ballDetail);
            System.out.println(ballDetail);

            // Update players based on ball outcome
            if (outcome.isWicket()) {
                inningsUtil.incrementWickets();
                message = "Wicket! " + striker.getPlayer().getName() + " is out.";
                sendMatchUpdate(message);
                System.out.println(message);
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
        message = "Innings End: " + inningsUtil.getRuns() + "/" + inningsUtil.getWickets() + " in " + oversCompleted + "." + ballsInCurrentOver;
        sendMatchUpdate(message);
        System.out.println(message);

        message = "Player Stats:";
        sendMatchUpdate(message);
        for (PlayerObject player : batters) {
            message = player.getPlayer().getName() + ": " +
                    "Runs = " + player.getScore() +
                    ", Balls Faced = " + player.getBallsFaced() +
                    ", Fours = " + player.getFours() +
                    ", Sixes = " + player.getSixes() +
                    ", Dot Balls = " + player.getDotBalls() +
                    ", Singles = " + player.getSingles() +
                    ", Twos = " + player.getTwos() +
                    ", Threes = " + player.getThrees();
            sendMatchUpdate(message);
        }

        message = "Bowler Stats:";
        sendMatchUpdate(message);
        for (PlayerObject currBowler : bowlers) {
            message = currBowler.getPlayer().getName() + ": " +
                    "Overs Bowled = " + currBowler.getOversBowled() +
                    ", Wickets Taken = " + currBowler.getWicketsTaken();
            sendMatchUpdate(message);
        }
    }

    private  BallOutcomeUtil simulateBallEvent(InningsUtil inningsUtil, PlayerObject striker, PlayerObject nonStriker, PlayerObject bowler) {
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
    private static long getDelayBasedOnEvent(int runs, int wickets) {
        if (wickets >= 1) {
            return 3; // Longer delay for a wicket
        } else if (runs % 6 == 0) {
            return 2; // Delay for a six
        } else {
            return 1; // Normal delay
        }
    }



}
