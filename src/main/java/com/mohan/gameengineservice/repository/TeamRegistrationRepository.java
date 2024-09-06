package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.dto.TeamSummary;
import com.mohan.gameengineservice.entity.TeamRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


public interface TeamRegistrationRepository extends JpaRepository<TeamRegistration, Long> {

    List<TeamRegistration> findTeamRegistrationByTournamentId(Long tournamentId);
    long countTeamRegistrationByTournamentId(Long tournamentId);
    long countByTournamentIdAndGroupType(Long tournamentId, String group);

    @Query("SELECT t FROM TeamRegistration tr JOIN tr.team t WHERE tr.tournament.id = :tournamentId")
    List<TeamSummary> findTeamSummariesByTournamentId(@Param("tournamentId") Long tournamentId);

//    @Query("SELECT t.teamId AS id, t.name AS name, t.country AS country, t.coach AS coach, t.owner AS owner " +
//            "FROM Team t WHERE t.coachId = :coachId")
//    List<TeamSummary> findTeamSummariesByCoachId(@Param("coachId") Long coachId);

//    @Query("SELECT tr.team.id, tr.group FROM TeamRegistration tr WHERE tr.tournament.id = :tournamentId")
//    Map<Long, String> findTeamGroupMapByTournamentId(@Param("tournamentId") Long tournamentId);

    @Query("SELECT t.id AS teamId, t.groupType AS groupType FROM TeamRegistration t WHERE t.tournament.id = :tournamentId")
    Map<Long, String> findTeamGroupMapByTournamentId(@Param("tournamentId") Long tournamentId);

     boolean existsByTournamentIdAndGroupType(Long tournamentId, String group);
     boolean existsByTournamentIdAndTeamTeamId(Long tournamentId, long teamId);

//    @Query("SELECT t.groupType, COUNT(t) FROM TeamRegistration t WHERE t.tournament.id = :tournamentId GROUP BY t.groupType")
//    Map<String, Long> findTeamGroupMapByTournamentId(@Param("tournamentId") Long tournamentId);

    List<TeamRegistration> findTeamRegistrationByTournamentIdAndGroupType(Long tournamentId, String group);

}
