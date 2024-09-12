package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository  extends JpaRepository<Tournament, Long> {
}
