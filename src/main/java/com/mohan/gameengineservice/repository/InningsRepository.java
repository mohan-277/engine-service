package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Innings;
import com.mohan.gameengineservice.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InningsRepository extends JpaRepository<Innings, Long> {


 // this  is working but there is no innings is created as of now
//    List<Innings> findInningsByMatchId(Long matchId);
    @Query("SELECT i FROM Innings i WHERE i.cricketMatch.id = :matchId")
    List<Innings> findInningsByMatchId(@Param("matchId") Long matchId);

}
