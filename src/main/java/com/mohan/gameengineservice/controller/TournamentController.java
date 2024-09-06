package com.mohan.gameengineservice.controller;


import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.mohan.gameengineservice.dto.*;
import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Tournament;
import com.mohan.gameengineservice.entity.constants.TournamentStatus;
import com.mohan.gameengineservice.exceptions.TeamNotFoundException;
import com.mohan.gameengineservice.repository.TournamentRepository;
import com.mohan.gameengineservice.service.TournamentService;
import com.mohan.gameengineservice.service.impl.MatchSchedulingService;
import com.mohan.gameengineservice.service.impl.MatchService;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/admin/tournaments")
@CrossOrigin("http://localhost:3000/")
public class TournamentController {


    TournamentService tournamentService;
    MatchSchedulingService matchSchedulingService;

    public TournamentController(TournamentService tournamentService, MatchSchedulingService matchSchedulingService) {
        this.tournamentService = tournamentService;
        this.matchSchedulingService = matchSchedulingService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTournament( @RequestBody  TournamentDTO tournamentDTO) {
        try {
            Tournament savedTournament = tournamentService.createTournament(tournamentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTournament);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the tournament");
        }
    }


    // this is for the tournament created by admin
    @GetMapping("/get-all-tournaments")
    public ResponseEntity<List<TournamentDTO>> getAllTournamentsCreatedAdmin() {
            return ResponseEntity.ok(tournamentService.getAllTournaments());
    }


    // registering or signup  the team into tournament with the particular id
    @PostMapping("/{tournamentId}/register")
    public ResponseEntity<?> registerTeamByTournamentID (@PathVariable Long tournamentId, @RequestBody TeamRegistrationDTO teamRegistrationDTO) {
        try {
            String result = tournamentService.registerTeamByTournamentID(tournamentId, teamRegistrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (TeamNotFoundException e) {
            // Handle team not found scenario
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during team registration");
        }
    }

//    @PostMapping("/tournament/{id}/register")
//    public ResponseEntity<?> registerTeamForTournament(@PathVariable Long id, @RequestBody TeamRegistrationDTO tournamentDTO) {
//        try {
//            tournamentService.registerTeam(id, tournamentDTO);
//            return ResponseEntity.ok("Team successfully registered for the tournament.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }



    // this will show how many team registered in the particular tournament I'd admin access
    @GetMapping("/{tournamentId}/teams")
    public ResponseEntity<List<TeamSummaryDTO>> getRegisteredTeams(@PathVariable Long tournamentId) {
        try {
            List<TeamSummaryDTO> teams = tournamentService.getRegisteredTeams(tournamentId);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    // this is for the schedule api after registering the 6 team then it need to hit this api
    @PostMapping("/{tournamentId}/schedule")
    public ResponseEntity<?> scheduleRoundRobin(@PathVariable Long tournamentId) {
        try {
            List<MatchDetailsDTO> matchDetails = matchSchedulingService.scheduleGroupStageMatches(tournamentId);
            return ResponseEntity.ok(matchDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    // after scheduling the match it will give the list of play-offs, semifinals, final
    @GetMapping("/{tournamentId}/matches")
    public ResponseEntity<Map<String, Map<String, List<MatchDetailsDTO>>>> getMatchesByTournamentId(@PathVariable Long tournamentId) {
        System.out.println("testing called");
//        Map<String, Map<String, List<MatchDetailsDTO>>> matches = matchSchedulingService.getMatchesByTypeAndGroup(tournamentId);
//        return ResponseEntity.ok(matches);
        return null;
    }





}
