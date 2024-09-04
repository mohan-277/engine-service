package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository  extends JpaRepository<Tournament, Long> {
}
