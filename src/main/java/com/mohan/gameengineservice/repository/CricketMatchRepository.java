package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Team;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CricketMatchRepository extends JpaRepository<CricketMatch, Long>  {


    boolean existsByTeamAAndTeamBAndMatchDateTime(Team teamA, Team teamB, LocalDateTime matchDateTime);

    List<CricketMatch> findCricketMatchesByTournamentId(Long tournamentId);



    @Query("SELECT cm FROM CricketMatch cm WHERE cm.matchType = :matchType AND cm.matchGroup = :matchGroup")
    List<CricketMatch> findByMatchTypeAndGroup(@Param("matchType") String matchType, @Param("matchGroup") String matchGroup);

    @Query(value = "SELECT * FROM testDb.cricket_matches WHERE tournament_id = ?1", nativeQuery = true)
    List<CricketMatch> findMatchesByTournamentId(Long tournamentId);

//    @Query(value = "SELECT * FROM CricketMatch WHERE tournament_id = ?1", nativeQuery = true)
//    List<CricketMatch> findMatchesByTournamentId(Long tournamentId);


}
