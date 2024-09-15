package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.entity.CricketMatch;
import com.sbear.gameengineservice.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CricketMatchRepository extends JpaRepository<CricketMatch, Long>  {


    boolean existsByTeamAAndTeamBAndMatchDateTime(Team teamA, Team teamB, LocalDateTime matchDateTime);

    boolean existsByTournamentIdAndMatchStage(Long tournamentId, String matchStage);


    @Query("SELECT COUNT(m) = (SELECT COUNT(m2) FROM CricketMatch m2 WHERE m2.tournament.id = :tournamentId AND m2.matchGroup = :matchGroup AND m2.matchStage = :matchStage) " +
            "FROM CricketMatch m WHERE m.tournament.id = :tournamentId AND m.matchGroup = :matchGroup AND m.matchStage = :matchStage AND m.matchStatus = 'Completed'")
    boolean allMatchesInGroupCompletedForStage(@Param("tournamentId") Long tournamentId, @Param("matchGroup") String matchGroup, @Param("matchStage") String matchStage);

    long countByTournamentIdAndMatchStageAndMatchStatus(Long tournamentId, String matchStage, String matchStatus);


    @Query(value = "SELECT * FROM cricket_matches WHERE tournament_id = ?1", nativeQuery = true)
    List<CricketMatch> findMatchesByTournamentId(Long tournamentId);


}
