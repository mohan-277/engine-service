package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.entity.stats.StatusOfMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusOfMatchRepository extends JpaRepository<StatusOfMatch, Long> {

    StatusOfMatch findTopByMatchStageNameOrderByCountDesc(String name);

    @Query("SELECT s.count FROM StatusOfMatch s WHERE s.matchStageName = :name ORDER BY s.count DESC")
    Long findTopCountByNameOrderByCountDesc(@Param("name") String matchStageName);



}
