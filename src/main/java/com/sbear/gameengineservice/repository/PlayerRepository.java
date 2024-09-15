package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findAllByCountry(String country);

    @Query("SELECT p FROM Player p WHERE p.name = :name AND p.dateOfBirth = :dateOfBirth")
    Optional<Player> findByNameAndDateOfBirth(String name, LocalDate dateOfBirth);

    @Query("SELECT DISTINCT TRIM(UPPER(t.country)) FROM Player t")
    List<String> findDistinctCountries();


    List<Player> findByTeamId(Long teamId);
}
