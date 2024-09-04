package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CricketMatchRepository extends JpaRepository<CricketMatch, Long>  {


    boolean existsByTeamAAndTeamBAndMatchDateTime(Team teamA, Team teamB, LocalDateTime matchDateTime);

    List<CricketMatch> findCricketMatchesByTournamentId(Long tournamentId);
}
