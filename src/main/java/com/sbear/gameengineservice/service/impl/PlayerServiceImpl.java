package com.sbear.gameengineservice.service.impl;

import com.sbear.gameengineservice.dto.PlayerScoreCardDTO;
import com.sbear.gameengineservice.entity.stats.PlayerStats;
import com.sbear.gameengineservice.repository.PlayerRepository;
import com.sbear.gameengineservice.repository.stats.PlayerStatsRepository;
import com.sbear.gameengineservice.service.PlayerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {


    private final PlayerStatsRepository playerStatsRepository;
    public PlayerServiceImpl(PlayerRepository playerRepository, PlayerStatsRepository playerStatsRepository) {
        this.playerStatsRepository = playerStatsRepository;
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
}
