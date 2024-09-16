package com.sbear.gameengineservice.controller;

import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.CricketMatch;
import com.sbear.gameengineservice.exceptions.MatchesNotFoundException;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import com.sbear.gameengineservice.service.MatchService;
import com.sbear.gameengineservice.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/engine-service/match")
@CrossOrigin("http://localhost:3000/")
public class MatchController {

    TeamService teamService;
    MatchService matchService;

    public MatchController( TeamService teamService, MatchService matchService) {
        this.teamService = teamService;
        this.matchService = matchService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMatches() {
        try {
            List<MatchDetailsDTO> matches = matchService.getAllMatches();
            if (matches == null &&  matches.isEmpty() ) {
                throw new MatchesNotFoundException("No matches found");
            }
            return ResponseEntity.ok(matches);
        } catch (MatchesNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMatch(@RequestBody CricketMatch cricketMatch) {
        try {
            if (cricketMatch == null) {
                return ResponseEntity.badRequest().body("Cricket match details cannot be null");
            }
            CricketMatch savedMatch = matchService.addMatch(cricketMatch);
            return ResponseEntity.status(HttpStatus.CREATED).body("Match added successfully with ID: " + savedMatch.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }



    @PostMapping("/create")
    public ResponseEntity<MatchDetailsDTO> createMatch(@RequestBody MatchDetailsDTO matchDetailsDTO) {
        try {
            MatchDetailsDTO createdMatch = matchService.createMatch(matchDetailsDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMatch);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @GetMapping("/get-match/{matchId}")
    public ResponseEntity<?> getMatch(@PathVariable Long matchId) {
        try {
            MatchDetailsDTO matchDetails = matchService.matchDetailsGetByMatchId(matchId);
            if (matchDetails == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Match not found with ID: " + matchId);
            }
            return ResponseEntity.ok(matchDetails);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // tournament related
    @GetMapping("/get-count/{matchStageName}")
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


}

