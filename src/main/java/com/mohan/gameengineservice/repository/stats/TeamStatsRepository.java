package com.mohan.gameengineservice.repository.stats;

import com.mohan.gameengineservice.entity.stats.TeamStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamStatsRepository extends JpaRepository<TeamStats, Integer> {
    TeamStats findByTeamName(String teamName);


    TeamStats findTeamStatsByMatchIdAndTeamName(Long matchId, String teamName);
}
