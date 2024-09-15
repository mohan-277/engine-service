package com.sbear.gameengineservice.service.impl;

import com.sbear.gameengineservice.entity.Ball;
import com.sbear.gameengineservice.service.BallRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class BallRepositoryImpl implements BallRepositoryCustom {


    @PersistenceContext
    private EntityManager entityManager;


    public void saveBall(Ball ball) {
        if (ball.getId() != null) {
            Ball existingBall = entityManager.find(Ball.class, ball.getId());
            if (existingBall != null) {
                entityManager.merge(ball); // Update existing entity
            } else {
                entityManager.persist(ball); // Persist new entity
            }
        } else {
            entityManager.persist(ball); // Persist new entity
        }
    }

}
