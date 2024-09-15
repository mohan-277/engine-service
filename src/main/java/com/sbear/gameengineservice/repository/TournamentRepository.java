package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository  extends JpaRepository<Tournament, Long> {
}
