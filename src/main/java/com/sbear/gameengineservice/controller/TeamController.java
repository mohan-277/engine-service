package com.sbear.gameengineservice.controller;

import com.sbear.gameengineservice.dto.PlayerDTO;
import com.sbear.gameengineservice.dto.TeamDTO;
import com.sbear.gameengineservice.dto.TeamSummary;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import com.sbear.gameengineservice.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/engine-service/teams")
@CrossOrigin("http://localhost:3000/")
public class TeamController {


    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;

    }


    @GetMapping("/list-teams")
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        try {
            List<TeamDTO> teamDTOs = teamService.getAllTeams();
            return new ResponseEntity<>(teamDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/list-teams-summary")
    public ResponseEntity<List<TeamSummary>> getAllTeamSummary() {
        try {
            List<TeamSummary> teamSummaries = teamService.getAllTeamSummaries();
            return new ResponseEntity<>(teamSummaries, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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


    @PostMapping("/create-team")
    public ResponseEntity<String> createTeam(@RequestBody TeamDTO teamDTO) {
        try {
            teamService.createTeam(teamDTO);
            return new ResponseEntity<>("Team created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating team: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/get/{teamId}")
    public ResponseEntity<?> getTeamByTeamId(@PathVariable Integer teamId) {
        try {
            TeamDTO teamDTO = teamService.getTeamById(teamId);
            return ResponseEntity.ok(teamDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found");
        }
    }


    @PostMapping("/{teamId}/players")
    public ResponseEntity<String> addPlayersToTeam(@PathVariable Integer teamId, @RequestBody List<PlayerDTO> playerDTOs) {
        teamService.setPlayersForTeam(teamId, playerDTOs);
        return ResponseEntity.ok("Players added successfully");
    }


}
