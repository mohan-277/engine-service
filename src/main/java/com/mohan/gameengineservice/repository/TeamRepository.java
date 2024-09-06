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

//    @Query("SELECT t.teamId AS teamID, t.name AS name, t.country AS country, t.teamCaptain AS teamCaptain, t.coach AS coach, t.owner AS owner FROM Team t")
//    List<TeamSummary> findAllTeamSummaries();

    @Query("SELECT t.teamId AS teamID, t.name AS name, t.country AS country, t.teamCaptain AS teamCaptain, t.coach AS coach, t.owner AS owner, t.coachId AS coachId FROM Team t")
    List<TeamSummary> findAllTeamSummaries();

    List<Team> findByTournament(Tournament tournament);

//    List<Team> findTournamentId(Long tournamentId);


    @Query("SELECT t.teamId AS teamID, t.name AS name, t.country AS country, t.teamCaptain AS teamCaptain, t.coach AS coach, t.owner AS owner, t.coachId AS coachId " +
            "FROM Team t WHERE t.coachId = :coachId")
    TeamSummary findTeamSummaryByCoachId(@Param("coachId") Long coachId);


    @Query("SELECT t FROM Team t WHERE t.tournament.id = :tournamentId")
    List<Team> findTeamsByTournamentId(@Param("tournamentId") Long tournamentId);
}
