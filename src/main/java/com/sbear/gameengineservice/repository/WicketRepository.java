package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.entity.Wicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WicketRepository extends JpaRepository<Wicket, Long> {
}
