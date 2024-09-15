package com.sbear.gameengineservice.repository.stats;

import com.sbear.gameengineservice.entity.stats.TeamStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamStatsRepository extends JpaRepository<TeamStats, Integer> {
    TeamStats findByTeamName(String teamName);


    TeamStats findTeamStatsByMatchIdAndTeamName(Long matchId, String teamName);

    List<TeamStats> findTeamStatsByMatchId(Long matchId);

    @Query("SELECT SUM(ps.points) FROM TeamStats ps WHERE ps.teamName = :teamName")
    Integer findTotalPointsByTeamName(@Param("teamName") String teamName);

    @Query(value = "SELECT t FROM TeamStats t WHERE t.matchGroup = :matchGroup" )
    List<TeamStats> findTeamStatsByMatchGroup(@Param("matchGroup") String matchGroup);

    @Query("SELECT t FROM TeamStats t WHERE t.matchGroup = :matchGroup AND t.points = 2")
    List<TeamStats> findTop2TeamsByMatchGroupAndPoints(@Param("matchGroup") String matchGroup);

}
