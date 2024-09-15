package com.sbear.gameengineservice.controller;

import com.sbear.gameengineservice.dto.*;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.exceptions.ErrorResponse;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import com.sbear.gameengineservice.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.format.DateTimeParseException;
import java.util.*;



@RestController
@RequestMapping("/api/engine-service/player")
@CrossOrigin("http://localhost:3000/")
public class PlayerController {


    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }



    @PostMapping({"/player/create-profile", "/coach/player/create-profile"})
    public ResponseEntity<?> registerPlayer(@RequestBody PlayerDTO playerDTO) {
        try {
            Player savedPlayer = playerService.registerPlayer(playerDTO);
            return ResponseEntity.ok(savedPlayer);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format: " + e.getMessage());
        }
    }

    @GetMapping("/player/profile/{id}")
    public ResponseEntity<?> getPlayer(@PathVariable Long id) {
        try {
            PlayerDTO playerDTO = playerService.getPlayerById(id);
            return ResponseEntity.ok(playerDTO);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/list-players/{country}")
    public ResponseEntity<?> getAllPlayers(@PathVariable String country) {
        try {
            List<PlayerDTO> playerDTOs = playerService.getAllPlayersByCountry(country);
            return ResponseEntity.ok(playerDTOs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/list-players")
    public ResponseEntity<?> getAllPlayers() {
        try {
            List<PlayerDTO> playerDTOs = playerService.getAllPlayers();
            return ResponseEntity.ok(playerDTOs);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/list-players/country")
    public ResponseEntity<List<String>> getAllPlayersCountry() {
        return new ResponseEntity<>(playerService.getAllPlayersCountry(), HttpStatus.OK);
    }


    @GetMapping("/player-score-card/{playerName}")
    public PlayerScoreCardDTO getPlayerScoreCard(@PathVariable String playerName) {
        return playerService.getPlayerScoreCard(playerName);
    }




}
