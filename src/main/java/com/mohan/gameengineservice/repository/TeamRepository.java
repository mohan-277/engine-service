package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.dto.TeamSummary;
import com.mohan.gameengineservice.entity.Team;
import com.mohan.gameengineservice.entity.TeamRegistration;
import com.mohan.gameengineservice.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TeamRepository  extends JpaRepository<Team, Integer> {

    @Query("SELECT t FROM Team t")
    List<TeamSummary> findAllTeamSummaries();

    List<Team> findByTournament(Tournament tournament);

//    List<Team> findTournamentId(Long tournamentId);

    @Query("SELECT t FROM Team t WHERE t.tournament.id = :tournamentId")
    List<Team> findTeamsByTournamentId(@Param("tournamentId") Long tournamentId);
}
