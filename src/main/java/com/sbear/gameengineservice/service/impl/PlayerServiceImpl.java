package com.sbear.gameengineservice.service.impl;

import com.sbear.gameengineservice.dto.PlayerDTO;
import com.sbear.gameengineservice.dto.PlayerScoreCardDTO;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.stats.PlayerStats;
import com.sbear.gameengineservice.exceptions.PlayerNotFoundException;
import com.sbear.gameengineservice.exceptions.ResourceNotFoundException;
import com.sbear.gameengineservice.repository.PlayerRepository;
import com.sbear.gameengineservice.repository.stats.PlayerStatsRepository;
import com.sbear.gameengineservice.service.PlayerService;
import com.sbear.gameengineservice.mappers.PlayerMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {


    private final PlayerStatsRepository playerStatsRepository;

    private final PlayerRepository playerRepository;



    public PlayerServiceImpl(PlayerRepository playerRepository, PlayerStatsRepository playerStatsRepository) {
        this.playerStatsRepository = playerStatsRepository;
        this.playerRepository = playerRepository;

    }


    public PlayerScoreCardDTO getPlayerScoreCard(String playerName) {
        List<PlayerStats> statsList = playerStatsRepository.findByPlayerName(playerName);

        if (statsList.isEmpty()) {
            throw new RuntimeException("No stats found for player: " + playerName);
        }

        // Aggregate data
        String teamName = statsList.get(0).getTeamName();
        String playerType = statsList.get(0).getPlayerType();

        Long totalRuns = statsList.stream().mapToLong(PlayerStats::getRuns).sum();
        Integer totalBallsFaced = statsList.stream().mapToInt(PlayerStats::getBallsFaced).sum();
        Integer totalFours = statsList.stream().mapToInt(PlayerStats::getFours).sum();
        Integer totalSixes = statsList.stream().mapToInt(PlayerStats::getSixes).sum();
        Integer totalDotBalls = statsList.stream().mapToInt(PlayerStats::getDotBalls).sum();
        Integer totalSingles = statsList.stream().mapToInt(PlayerStats::getSingles).sum();
        Integer totalTwos = statsList.stream().mapToInt(PlayerStats::getTwos).sum();
        Integer totalThrees = statsList.stream().mapToInt(PlayerStats::getThrees).sum();
        Integer totalOversBowled = statsList.stream().mapToInt(PlayerStats::getOversBowled).sum();
        Integer totalWicketsTaken = statsList.stream().mapToInt(PlayerStats::getWicketsTaken).sum();

        return new PlayerScoreCardDTO(
                playerName, teamName, playerType,
                totalRuns, totalBallsFaced, totalFours, totalSixes,
                totalDotBalls, totalSingles, totalTwos, totalThrees,
                totalOversBowled, totalWicketsTaken
        );
    }



    public Player registerPlayer(PlayerDTO playerDTO) throws DateTimeParseException {
        LocalDate dateOfBirth = LocalDate.parse(playerDTO.getDateOfBirth());
        Player player = Player.builder()
                .name(playerDTO.getName())
                .country(playerDTO.getCountry())
                .dateOfBirth(dateOfBirth)
                .gender(playerDTO.getGender())
                .specialization(playerDTO.getSpecialization())
                .build();
        return playerRepository.save(player);
    }

    public PlayerDTO getPlayerById(Long id) throws PlayerNotFoundException{
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(Long.toString(id)));
        return PlayerMapper.toDTO(player);
    }


   public List<PlayerDTO> getAllPlayersByCountry(String country){
       List<Player> players = playerRepository.findAllByCountry(country);
       if (players.isEmpty()) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No players found for country: " + country);
       }
       return players.stream()
               .map(PlayerMapper::toDTO)
               .collect(Collectors.toList());
   }

    public List<PlayerDTO> getAllPlayers(){
        List<Player> players = playerRepository.findAll();
        if (players.isEmpty()) {
            throw new ResourceNotFoundException("No players found");
        }
        return players.stream()
                .map(PlayerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllPlayersCountry(){
        return playerRepository.findDistinctCountries();
    }

}
