package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.dto.TeamSummary;
import com.sbear.gameengineservice.entity.Team;
import com.sbear.gameengineservice.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TeamRepository  extends JpaRepository<Team, Integer> {

    @Query("SELECT t.teamId AS teamID, t.name AS name, t.country AS country, t.teamCaptain AS teamCaptain, t.coachName AS coachName, t.owner AS owner, t.coachId AS coachId FROM Team t")
    List<TeamSummary> findAllTeamSummaries();

    List<Team> findByTournament(Tournament tournament);



    @Query("SELECT t.teamId AS teamID, t.name AS name, t.country AS country, t.teamCaptain AS teamCaptain, t.coachName AS coachName, t.owner AS owner, t.coachId AS coachId " +
            "FROM Team t WHERE t.coachId = :coachId")
    TeamSummary findTeamSummaryByCoachId(@Param("coachId") Long coachId);


    @Query("SELECT t FROM Team t WHERE t.tournament.id = :tournamentId")
    List<Team> findTeamsByTournamentId(@Param("tournamentId") Long tournamentId);

    Optional<Team> findByName(String name);

    Team findTeamByName(String name);

    @Query("SELECT MAX(t.coachId) FROM Team t")
    Long findMaxCoachId();
}
