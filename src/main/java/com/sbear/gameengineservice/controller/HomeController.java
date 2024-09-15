package com.sbear.gameengineservice.controller;

import com.sbear.gameengineservice.dto.*;
import com.sbear.gameengineservice.entity.CricketMatch;
import com.sbear.gameengineservice.entity.Innings;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.Team;
import com.sbear.gameengineservice.exceptions.MatchesNotFoundException;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import com.sbear.gameengineservice.repository.CricketMatchRepository;
import com.sbear.gameengineservice.repository.PlayerRepository;
import com.sbear.gameengineservice.repository.TeamRepository;
import com.sbear.gameengineservice.service.HomeService;
import com.sbear.gameengineservice.service.PlayerService;
import com.sbear.gameengineservice.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/engine-service")
@CrossOrigin("http://localhost:3000/")
public class HomeController {

    CricketMatchRepository cricketMatchRepository;
    PlayerRepository playerRepository;
    TeamRepository teamRepository;
    TeamService teamService;
    HomeService homeService;
    PlayerService playerService;

    public HomeController(CricketMatchRepository cricketMatchRepository, PlayerRepository playerRepository, TeamRepository teamRepository, TeamService teamService, HomeService homeService, PlayerService playerService) {
        this.cricketMatchRepository = cricketMatchRepository;
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.teamService = teamService;
        this.homeService = homeService;
        this.playerService = playerService;
    }
    @GetMapping("/get-matches")
    public ResponseEntity<?> getAllMatches() {
        try {
            List<CricketMatch> matches = homeService.getAllMatches();
            if (matches.isEmpty()) {
                throw new MatchesNotFoundException("No matches found");
            }
            return ResponseEntity.ok(matches);
        } catch (MatchesNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/matches")
    public  void  addMatch(@RequestBody  CricketMatch cricketMatch){
        cricketMatchRepository.save(cricketMatch);
    }

    // this is working api while doing with the front end integration so store it
    @PostMapping("/player/profile")
    public ResponseEntity<Player> registerPlayer(@RequestParam("name") String name,
                                                 @RequestParam("dateOfBirth") String dateOfBirth,
                                                 @RequestParam("specialization") String specialization,
                                                 @RequestParam("gender") String gender,
                                                 @RequestParam("country") String country,
                                                 @RequestParam("profilePicture") MultipartFile profilePicture
                                                 ) throws IOException {

        Player player = new Player();
        player.setName(name);
        player.setDateOfBirth(LocalDate.parse(dateOfBirth));
        player.setSpecialization(specialization);
        player.setGender(gender);
        player.setCountry(country);
        player.setProfilePicture(profilePicture.getBytes());

        Player savedPlayer = playerRepository.save(player);
        System.out.println("this is player profile is called");
        return ResponseEntity.ok(savedPlayer);
    }

    @PostMapping({"/player/create-profile", "/coach/player/create-profile"})
    public ResponseEntity<?> registerPlayer(@RequestBody PlayerDTO playerDTO) {

        try {
            LocalDate dateOfBirth = LocalDate.parse(playerDTO.getDateOfBirth());
            Player player = Player.builder()
                    .name(playerDTO.getName())
                    .country(playerDTO.getCountry())
                    .dateOfBirth(dateOfBirth)
                    .gender(playerDTO.getGender())
                    .specialization(playerDTO.getSpecialization())
                    .build();
            Player savedPlayer = playerRepository.save(player);
            return ResponseEntity.ok(savedPlayer);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format: " + e.getMessage());
        }
    }




    @GetMapping("/player/profile/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable Long id) {
        Player player = playerRepository.findById(id).orElseThrow(() -> new RuntimeException("Player not found"));
        return ResponseEntity.ok(player);
    }

    @GetMapping("/list-players/{country}")
    public ResponseEntity<List<PlayerDTO>> getAllPlayers(@PathVariable String country) {
        List<Player> players = playerRepository.findAllByCountry(country);
        return new ResponseEntity<>(players.stream()
                .map(player -> {
                    PlayerDTO dto = new PlayerDTO();
                    dto.setId(player.getId());
                    dto.setName(player.getName());
                    dto.setDateOfBirth(String.valueOf(player.getDateOfBirth()));
                    dto.setSpecialization(player.getSpecialization());
                    dto.setGender(player.getGender());
                    dto.setCountry(player.getCountry());
                    dto.setPlayedMatches(player.getPlayedMatches()); // Ensure these fields are set
                    dto.setRuns(player.getRuns());
                    dto.setWickets(player.getWickets());
                    dto.setHighScore(player.getHighScore());
                    dto.setProfilePicture(player.getProfilePicture()); // Optional
                    return dto;
                })
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/list-players")
    public ResponseEntity<List<Player>> getAllPlayers() {

        return new ResponseEntity<>(playerRepository.findAll(), HttpStatus.OK);
    }

    /// this is for the only country name will return
    @GetMapping("/list-players/country")
    public ResponseEntity<List<String>> getAllPlayersCountry() {
        return new ResponseEntity<>(homeService.getAllPlayersCountry(), HttpStatus.OK);
    }


    /// this is also need to take care of the lazy loading
    @GetMapping("/list-teams")
    public ResponseEntity<List<Team>> getAllTeams() {
        return new ResponseEntity<>(teamRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/list-teams-summary")
    public ResponseEntity<List<TeamSummary>> getAllTeamSummary() {
        return new ResponseEntity<>(teamRepository.findAllTeamSummaries(), HttpStatus.OK);
    }


    @GetMapping("/list-teams-summary/{coachId}")
    public ResponseEntity<?> getAllTeamSummary(@PathVariable Long coachId) {
        try {
            TeamSummary teamSummary = teamService.getTeamSummary(coachId);
            return ResponseEntity.ok(teamSummary);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("coach id not found");
        }
    }

//    @GetMapping("/get-team/{teamId}")
//    public ResponseEntity<TeamDTO> getTeamByTeamId(@PathVariable Integer teamId) {
//
//        return new ResponseEntity<>(teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found")), HttpStatus.OK);
//    }


    /// here this needs to be refracted because, while creating the team,
    /// it is giving the duplicate data I need to use lazy or change the return type
    @PostMapping("/create-team")
    public ResponseEntity<?> createTeam(@RequestBody TeamDTO teamDTO) {

        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setCountry(teamDTO.getCountry());
        team.setTeamCaptain(teamDTO.getTeamCaptain());
        team.setOwner(teamDTO.getOwner());
        team.setCoachName(teamDTO.getCoach());

        List<Player> players = teamDTO.getPlayers().stream().map(dto -> {
            // Check if the player already exists by name and dateOfBirth
            Optional<Player> existingPlayerOpt = playerRepository.findByNameAndDateOfBirth(dto.getName(), LocalDate.parse(dto.getDateOfBirth()));

            Player player;
            if (existingPlayerOpt.isPresent()) {
                player = existingPlayerOpt.get();
            } else {
                // If the player doesn't exist, create a new one
                player = new Player();
                player.setName(dto.getName());
                player.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
                player.setSpecialization(dto.getSpecialization());
                player.setGender(dto.getGender());
                player.setCountry(dto.getCountry());
                player = playerRepository.save(player);
            }

            // Add the player to the team
            player.setTeam(team);
            return player;
        }).collect(Collectors.toList());
        team.setCoachId(generateNextCoachId());
        team.setPlayers(players);
        teamRepository.save(team);
        return new ResponseEntity<>("created", HttpStatus.CREATED);
    }

    public Long generateNextCoachId() {
        Long maxCoachId = teamRepository.findMaxCoachId(); // Custom method to find max coachId
        return (maxCoachId == null) ? 1L : maxCoachId + 1;
    }

    // get teams by id it will help to create a match in between the team
    /// I think it is not using to create a match as of now
    @PostMapping("/create-match")
    public ResponseEntity<CricketMatch> createMatch(@RequestBody MatchRequest request) {
        Team teamA = teamRepository.findById(request.getTeamAId()).orElseThrow(() -> new RuntimeException("Team A not found"));
        Team teamB = teamRepository.findById(request.getTeamBId()).orElseThrow(() -> new RuntimeException("Team B not found"));
        Team battingTeam = teamRepository.findById(request.getBattingTeamId()).orElseThrow(() -> new RuntimeException("Batting team not found"));
        Team bowlingTeam = teamRepository.findById(request.getBowlingTeamId()).orElseThrow(() -> new RuntimeException("Bowling team not found"));

        // Create match
        CricketMatch match = new CricketMatch();
        match.setStadiumName(request.getStadiumName());
        match.setMatchType(request.getMatchType());
        match.setMatchDateTime(request.getMatchStartTime());
        match.setTeamA(teamA);
        match.setTeamB(teamB);

        // Set innings based on batting and bowling teams
        Innings firstInnings = new Innings();
        firstInnings.setBattingTeam(battingTeam);
        firstInnings.setBowlingTeam(bowlingTeam);
        firstInnings.setMatch(match);
        match.getInnings().add(firstInnings);

        return ResponseEntity.ok(match);
    }

    @GetMapping("/get-match/{matchId}")
    public MatchDetailsDTO getMatch(@PathVariable Long matchId) {
        return homeService.matchDetailsGetByMatchId(matchId);
    }

    @PostMapping("/{teamId}/players")
    public ResponseEntity<String> addPlayersToTeam(@PathVariable Long teamId, @RequestBody List<PlayerDTO> playerDTOs) {
        teamService.setPlayersForTeam(teamId, playerDTOs);
        return ResponseEntity.ok("Players added successfully");
    }

    @GetMapping("/team/{teamId}")
    public TeamDTO getTeamByTeamId(@PathVariable Integer teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));
        return teamService.convertToTeamDto(team);
    }

    @GetMapping("/get-count-match-stages/{matchStageName}")
    public ResponseEntity<Object> getTheCountOfTheStagesStarted(@PathVariable String matchStageName) {
        try {
            Long count = teamService.getTheCountOfTheStagesStarted(matchStageName);
            if(count==null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>("Invalid StageName", HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/player-score-card/{playerName}")
    public PlayerScoreCardDTO getPlayerScoreCard(@PathVariable String playerName) {
        return playerService.getPlayerScoreCard(playerName);
    }


}
