package com.sbear.gameengineservice.controller;

import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import com.sbear.gameengineservice.service.TournamentService;
import com.sbear.gameengineservice.service.impl.MatchSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin/Match-scheduling")
@CrossOrigin("http://localhost:3000/")
public class MatchSchedulingController {


   private final MatchSchedulingService matchSchedulingService;
   private final TournamentService tournamentService;

    @Autowired
    public MatchSchedulingController(TournamentService tournamentService, MatchSchedulingService matchSchedulingService) {
        this.matchSchedulingService = matchSchedulingService;
        this.tournamentService = tournamentService;
    }

    @GetMapping("/semifinal/schedule-matches/{tournamentId}")
    public List<MatchDetailsDTO> getSemiFinalScheduleMatches(@PathVariable Long tournamentId){
        return matchSchedulingService.scheduleSemiFinals(tournamentId);
    }


    @GetMapping("/get-all-matches")
    public ResponseEntity<List<MatchDetailsDTO>> getAllMatches() {
        try {
            List<MatchDetailsDTO> matches = tournamentService.getAllMatches();
            if (matches.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }
            return ResponseEntity.ok(matches);
        } catch (ResourceNotFoundException e) {
            System.err.println("No matches found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        } catch (Exception e) {
            System.err.println("An error occurred while retrieving match details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/final/schedule-matches/{tournamentId}")
    public MatchDetailsDTO getFinalScheduleMatches(@PathVariable Long tournamentId){
        return matchSchedulingService.getFinalScheduleMatches(tournamentId);
    }
}
