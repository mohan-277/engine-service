package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.dto.TeamSummary;
import com.mohan.gameengineservice.entity.TeamRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface TeamRegistrationRepository extends JpaRepository<TeamRegistration, Long> {

    List<TeamRegistration> findTeamRegistrationByTournamentId(Long tournamentId);
    long countTeamRegistrationByTournamentId(Long tournamentId);
    long countByTournamentIdAndGroup(Long tournamentId, String group);

    @Query("SELECT t FROM TeamRegistration tr JOIN tr.team t WHERE tr.tournament.id = :tournamentId")
    List<TeamSummary> findTeamSummariesByTournamentId(@Param("tournamentId") Long tournamentId);

    @Query("SELECT tr.team.id, tr.group FROM TeamRegistration tr WHERE tr.tournament.id = :tournamentId")
    Map<Long, String> findTeamGroupMapByTournamentId(@Param("tournamentId") Long tournamentId);

    List<TeamRegistration> findTeamRegistrationByTournamentIdAndGroup(Long tournamentId, String group);

}
