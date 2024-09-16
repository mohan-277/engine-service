package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.entity.Innings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InningsRepository extends JpaRepository<Innings, Long> {
   ;

    @Query("SELECT i FROM Innings i WHERE i.inningsNumber = :inningsNumber AND i.cricketMatch.id = :matchId")
    Optional<Innings> findByInningsNumberAndMatchId(@Param("inningsNumber") Long inningsNumber, @Param("matchId") Long matchId);

    List<Innings> findByCricketMatchId(Long matchId);
}
