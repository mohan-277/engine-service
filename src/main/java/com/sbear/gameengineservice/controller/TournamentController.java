package com.sbear.gameengineservice.controller;


import com.sbear.gameengineservice.dto.*;
import com.sbear.gameengineservice.entity.Tournament;
import com.sbear.gameengineservice.entity.stats.PlayerStats;
import com.sbear.gameengineservice.entity.stats.TeamStats;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import com.sbear.gameengineservice.service.TournamentService;
import com.sbear.gameengineservice.service.impl.MatchSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tournaments")
@CrossOrigin("http://localhost:3000/")
public class TournamentController {


    TournamentService tournamentService;
    MatchSchedulingService matchSchedulingService;



    @Autowired
    public TournamentController(TournamentService tournamentService, MatchSchedulingService matchSchedulingService) {
        this.tournamentService = tournamentService;
        this.matchSchedulingService = matchSchedulingService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTournament( @RequestBody TournamentDTO tournamentDTO) {
        try {
            Tournament savedTournament = tournamentService.createTournament(tournamentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTournament);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the tournament");
        }
    }


    /// this is for the tournament created by admin listing all the tournaments
    @GetMapping("/get-all-tournaments")
    public ResponseEntity<List<TournamentDTO>> getAllTournamentsCreatedAdmin() {
            return ResponseEntity.ok(tournamentService.getAllTournaments());
    }


    @PostMapping("/{tournamentId}/register")
    public ResponseEntity<?> registerTeamByTournamentID (@PathVariable Long tournamentId, @RequestBody TeamRegistrationDTO teamRegistrationDTO) {
        try {
            String result = tournamentService.registerTeamByTournamentID(tournamentId, teamRegistrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
         catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during team registration");
        }
    }




    /// this will show how many teams registered in the particular tournament I'd admin access
    @GetMapping("/{tournamentId}/teams")
    public ResponseEntity<List<TeamSummaryDTO>> getRegisteredTeams(@PathVariable Long tournamentId) {
        try {
            List<TeamSummaryDTO> teams = tournamentService.getRegisteredTeams(tournamentId);
            return ResponseEntity.ok(teams);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    // this is for the schedule api after successfully registered the 6 team then it needs to hit this api
    @PostMapping("/{tournamentId}/schedule/group-stages")
    public ResponseEntity<?> scheduleRoundRobin(@PathVariable Long tournamentId) {
        try {
            List<MatchDetailsDTO> matchDetails = matchSchedulingService.scheduleGroupStageMatches(tournamentId);
            return ResponseEntity.ok(matchDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    // after scheduling the match, it will give the list of play-offs, semifinals, final
    /// it is well worked for the group stage matches
    @GetMapping("/{tournamentId}/matches/group-stages")
    public ResponseEntity<?> getMatchesByTournamentId(@PathVariable Long tournamentId) {
        try {
            Map<String, List<MatchDetailsDTO>> matches = matchSchedulingService.getMatchesByTypeAndGroup(tournamentId);
            return ResponseEntity.ok(matches);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /// get tournament by id only
    @GetMapping("/get-tournament/{id}")
    public ResponseEntity<TournamentDTO> getTournamentById(@PathVariable Long id) {
        TournamentDTO tournamentDTO = tournamentService.getTournamentById(id);
        return ResponseEntity.ok(tournamentDTO);

    }


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

    @GetMapping("/player-stats/{matchId}")
    public ResponseEntity<List<PlayerStats>> getALlPlayerStatsByMatchId(@PathVariable Long matchId) {
        return new ResponseEntity<>(tournamentService.getALlPlayerStats(matchId), HttpStatus.OK);
    }
    @GetMapping("/team-stats/{matchId}")
    public ResponseEntity<List<TeamStats>>getAllTeamStatsByMatchId(@PathVariable Long matchId) {
        return new ResponseEntity<>(tournamentService.getAllTeamStats(matchId), HttpStatus.OK);
    }


    @GetMapping("/team-group/{groupType}")
    public List<TeamStats> getAllTeamStatsByGroupType(@PathVariable  String groupType) {
        if(groupType.equals("GroupA")){
            return tournamentService.getAllTeamStatsByMatchGroup("Group A");
        }else {
            return tournamentService.getAllTeamStatsByMatchGroup("Group B");
        }

    }



    @GetMapping("/semifinal/schedule-matches/{tournamentId}")
    public List<MatchDetailsDTO> getSemiFinalScheduleMatches(@PathVariable  Long tournamentId){
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

    /// this main ball by ball response sender api
    @GetMapping("/{matchId}/innings/balls")
    public MatchResponseDTO getBallDetails(@PathVariable Long matchId) {
        return tournamentService.getMatchDetails(matchId);
    }

}
