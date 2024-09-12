package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.Wicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WicketRepository extends JpaRepository<Wicket, Long> {
}
