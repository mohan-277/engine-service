package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.dto.TeamSummary;
import com.sbear.gameengineservice.entity.TeamRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TeamRegistrationRepository extends JpaRepository<TeamRegistration, Long> {

    List<TeamRegistration> findTeamRegistrationByTournamentId(Long tournamentId);

    long countByTournamentIdAndGroupType(Long tournamentId, String group);

     boolean existsByTournamentIdAndTeamTeamId(Long tournamentId, long teamId);

    List<TeamRegistration> findTeamRegistrationByTournamentIdAndGroupType(Long tournamentId, String group);

}
