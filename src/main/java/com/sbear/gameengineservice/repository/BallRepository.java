package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.entity.Ball;
import com.sbear.gameengineservice.service.BallRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BallRepository extends JpaRepository<Ball, Integer>, BallRepositoryCustom {


    List<Ball> findByInningsId(Long id);
}
