package com.mohan.gameengineservice.controller;


import com.mohan.gameengineservice.dto.*;
import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Tournament;
import com.mohan.gameengineservice.exceptions.ResourceNotFoundException;
import com.mohan.gameengineservice.exceptions.TeamNotFoundException;
import com.mohan.gameengineservice.service.TournamentService;
import com.mohan.gameengineservice.service.impl.MatchSchedulingService;
import com.mohan.gameengineservice.service.impl.MatchService;

import com.mohan.gameengineservice.service.impl.MatchService_Test;
import com.mohan.gameengineservice.websocket.services.CricketMatchV3Util;
import com.mohan.gameengineservice.websocket.CricketMatchSimulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tournaments")
@CrossOrigin("http://localhost:5173/")
public class TournamentController {



    private static final Logger logger = LoggerFactory.getLogger(TournamentController.class);



    TournamentService tournamentService;
    MatchSchedulingService matchSchedulingService;

    @Autowired
    CricketMatchV3Util cricketMatchV3Util;

    @Autowired
    private MatchService_Test matchServiceTest;


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
        System.out.println("testing");
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



    // this is for the schedule api after successfully  registered the 6 team then it need to hit this api
    @PostMapping("/{tournamentId}/schedule/group-stages")
    public ResponseEntity<?> scheduleRoundRobin(@PathVariable Long tournamentId) {
        try {
            List<MatchDetailsDTO> matchDetails = matchSchedulingService.scheduleGroupStageMatches(tournamentId);
            return ResponseEntity.ok(matchDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    // after scheduling the match it will give the list of play-offs, semifinals, final
    /// it is well working for the group stage matches
    @GetMapping("/{tournamentId}/matches/group-stages")
    public ResponseEntity<?> getMatchesByTournamentId(@PathVariable Long tournamentId) {
        logger.info("Fetching matches for tournament ID: {}", tournamentId);

        try {
            // Call the service method that returns DTOs
            Map<String, List<MatchDetailsDTO>> matches = matchSchedulingService.getMatchesByTypeAndGroup(tournamentId);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {
            // Log the exception and return an appropriate response
            logger.error("Error fetching matches for tournament ID: {}", tournamentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /// get tournament  by id only
    @GetMapping("/get-tournament/{id}")
    public ResponseEntity<TournamentDTO> getTournamentById(@PathVariable Long id) {
        TournamentDTO tournamentDTO = tournamentService.getTournamentById(id);
        return ResponseEntity.ok(tournamentDTO);

    }

    /// this is for the testing
    @PostMapping("/start/{matchId}")
    public String startMatch(@PathVariable Long matchId) {
        matchServiceTest.startMatch(matchId);
        return "Match simulation started for matchId " + matchId;
    }


    @PostMapping("/simulate")
    public String simulateMatch(@RequestBody MatchDetailsDTO matchDetailsDTO) {
//        matchSimulationService.simulateMatch(matchDetailsDTO);
        try {
//            cricketMatchSimulation.simulateMatchFromDTO(matchDetailsDTO);
            cricketMatchV3Util.simulateMatchFromDTO(matchDetailsDTO);

//            matchSimulationService.simulateMatch(matchDetailsDTO);
            return "Match simulation completed";
        } catch (InterruptedException e) {
            return "Error during simulation: " + e.getMessage();
        }
    }

    /*
    *   this is get the match details by the match ID  later i need to convert to the MatchDetails Dto so i can use this to get the simulate match
    */
    @GetMapping("/matches/{matchId}")
    public ResponseEntity<?> getMatchById(@PathVariable Long matchId) {
        try {
            MatchDetailsDTO match = tournamentService.getCricketMatchById(matchId);
            if (match != null) {
                return ResponseEntity.ok(match);
            } else {
                throw new ResourceNotFoundException("Match not found with ID: " + matchId);
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the match: " + e.getMessage());
        }
    }


}
