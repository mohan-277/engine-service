package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.CricketMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<CricketMatch, Long>  {
}
