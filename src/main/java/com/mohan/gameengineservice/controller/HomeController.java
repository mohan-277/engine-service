package com.mohan.gameengineservice.controller;

import com.mohan.gameengineservice.dto.MatchRequest;
import com.mohan.gameengineservice.dto.PlayerDTO;
import com.mohan.gameengineservice.dto.TeamDTO;
import com.mohan.gameengineservice.dto.TeamSummary;
import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Innings;
import com.mohan.gameengineservice.entity.Player;
import com.mohan.gameengineservice.entity.Team;
import com.mohan.gameengineservice.exceptions.ResourceNotFoundException;
import com.mohan.gameengineservice.repository.CricketMatchRepository;
import com.mohan.gameengineservice.repository.PlayerRepository;
import com.mohan.gameengineservice.repository.TeamRepository;
import com.mohan.gameengineservice.service.impl.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/engine-service")
@CrossOrigin("http://localhost:3000/")
public class HomeController {

    @Autowired
    CricketMatchRepository cricketMatchRepository;
    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    TeamRepository teamRepository;



    private final TeamService teamService;


    public HomeController(TeamService teamService) {
        this.teamService = teamService;
    }


    @GetMapping
    public String test(){
        return "Hello World";
    }


    @GetMapping("/get-matches")
    public List<CricketMatch> getALlMatches(){
        return cricketMatchRepository.findAll();
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

    @PostMapping("/test")
    public ResponseEntity<String> test(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok("Received payload: " + payload);
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
    public List<PlayerDTO> getAllPlayers(@PathVariable String country) {
        List<Player> players = playerRepository.findAllByCountry(country);
        return players.stream()
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
                    dto.setStrikeRate(player.getStrikeRate());
                    dto.setNumberOf50s(player.getNumberOf50s());
                    dto.setNumberOf100s(player.getNumberOf100s());
                    dto.setProfilePicture(player.getProfilePicture()); // Optional
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/list-players")
    public List<Player> getAllPlayers() {
        System.out.println("testing list of players");
        return playerRepository.findAll();
    }

    // this is for the only country name will return
    @GetMapping("/list-players/country")
    public List<String> getAllPlayersCountry() {
        return playerRepository.findDistinctCountries();
    }


    @GetMapping("/list-teams")
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @GetMapping("/list-teams-summary")
    public List<TeamSummary> getAllTeamSummary() {
        return teamRepository.findAllTeamSummaries();
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

    @GetMapping("/list-teams/{teamId}")
    public Team getAllTeams(@PathVariable Integer teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Team not found"));
    }


    @PostMapping("/create-team")
    public ResponseEntity<?> createTeam(@RequestBody TeamDTO teamDTO) {

        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setCountry(teamDTO.getCountry());
        team.setTeamCaptain(teamDTO.getTeamCaptain());
        team.setOwner(teamDTO.getOwner());
        team.setCoach(teamDTO.getCoach());

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

        team.setPlayers(players);
        teamRepository.save(team);
        return new ResponseEntity<>(team, HttpStatus.CREATED);
    }


    // get teams by  id it will help to create a match in between the team
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


    @PostMapping("/{teamId}/players")
    public ResponseEntity<String> addPlayersToTeam(@PathVariable Long teamId, @RequestBody List<PlayerDTO> playerDTOs) {
        teamService.setPlayersForTeam(teamId, playerDTOs);
        return ResponseEntity.ok("Players added successfully");
    }



}
