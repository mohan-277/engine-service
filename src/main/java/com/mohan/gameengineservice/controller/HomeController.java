package com.mohan.gameengineservice.controller;

import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Player;
import com.mohan.gameengineservice.entity.Team;
import com.mohan.gameengineservice.repository.MatchRepository;
import com.mohan.gameengineservice.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/engine-service")
@CrossOrigin("http://localhost:5173/")
public class HomeController {

    @Autowired
    MatchRepository matchRepository;
    @Autowired
    PlayerRepository playerRepository;


    @GetMapping
    public String test(){
        return "Hello World";
    }
    @GetMapping("/matches")
    public List<CricketMatch> getALlMatches(){
        return matchRepository.findAll();
    }
    @PostMapping("/matches")
    public  void  addMatch(@RequestBody  CricketMatch cricketMatch){
        matchRepository.save(cricketMatch);
    }

    @PostMapping("/profile")
    public ResponseEntity<Player> registerPlayer(@RequestParam("name") String name,
                                                 @RequestParam("dateOfBirth") String dateOfBirth,
                                                 @RequestParam("specialization") String specialization,
                                                 @RequestParam("gender") String gender,
                                                 @RequestParam("country") String country,
                                                 @RequestParam("profilePicture") MultipartFile profilePicture) throws IOException {

        Player player = new Player();
        player.setName(name);
        player.setDateOfBirth(LocalDate.parse(dateOfBirth));
        player.setSpecialization(specialization);
        player.setGender(gender);
        player.setCountry(country);
        player.setProfilePicture(profilePicture.getBytes());

        Player savedPlayer = playerRepository.save(player);
        return ResponseEntity.ok(savedPlayer);
    }





    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable Long id) {
        Player player = playerRepository.findById(id).orElseThrow(() -> new RuntimeException("Player not found"));
        return ResponseEntity.ok(player);
    }

    @GetMapping("/list-players")
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @GetMapping("/list-teams")
    public List<Team> getAllTeams() {
        return null;
    }
}
