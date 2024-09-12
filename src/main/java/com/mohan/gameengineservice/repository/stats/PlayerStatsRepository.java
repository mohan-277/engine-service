package com.mohan.gameengineservice.repository.stats;

import com.mohan.gameengineservice.entity.stats.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {

//   PlayerStats findByCurrentPlayingMatchIdAndPlayerName(Long matchId, String playerName);

//    @Query("SELECT p FROM PlayerStats p WHERE p.currentPlayingMatchId = :matchId AND p.playerName = :playerName")
//    PlayerStats findByMatchIdAndPlayerName(@Param("matchId") Long matchId, @Param("playerName") String playerName);

    PlayerStats findByCurrentPlayingMatchIdAndPlayerName(Long matchId, String playerName);

//    @Query(value = "SELECT * FROM gameDb.`player-stats` ps WHERE ps.current_playing_match_id = ?1 AND ps.player_name = ?2 ", nativeQuery = true)
//    PlayerStats findByCurrentPlayingMatchIdAndPlayerName(@Param("matchId") Long matchId, @Param("playerName") String playerName);

}
