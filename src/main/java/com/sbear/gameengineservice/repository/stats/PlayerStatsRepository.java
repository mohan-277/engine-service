package com.sbear.gameengineservice.repository.stats;

import com.sbear.gameengineservice.dto.PlayerScoreCardDTO;
import com.sbear.gameengineservice.entity.stats.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {


    @Query("SELECT ps FROM PlayerStats ps WHERE ps.currentPlayingMatchId = :matchId")
    List<PlayerStats> findByCurrentPlayingMatchId(@Param("matchId") Long matchId);

    @Query("SELECT ps FROM PlayerStats ps WHERE ps.playerName = :playerName")
    List<PlayerStats> findByPlayerName(@Param("playerName") String playerName);


}
