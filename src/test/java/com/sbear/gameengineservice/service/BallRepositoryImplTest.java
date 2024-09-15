package com.sbear.gameengineservice.service;
import static org.mockito.Mockito.*;
import com.sbear.gameengineservice.entity.Ball;
import com.sbear.gameengineservice.service.impl.BallRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class BallRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BallRepositoryImpl ballRepositoryImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveBallWhenIdIsNull() {
        // Given
        Ball ball = new Ball();
        ball.setId(null);

        // When
        ballRepositoryImpl.saveBall(ball);

        // Then
        verify(entityManager, times(1)).persist(ball);
        verify(entityManager, times(0)).merge(ball);
    }

    @Test
    public void testSaveBallWhenIdIsNotNullAndEntityExists() {
        // Given
        Ball ball = new Ball();
        ball.setId(1L);
        Ball existingBall = new Ball();
        existingBall.setId(1L);

        when(entityManager.find(Ball.class, 1L)).thenReturn(existingBall);

        // When
        ballRepositoryImpl.saveBall(ball);

        // Then
        verify(entityManager, times(1)).merge(ball);
        verify(entityManager, times(0)).persist(ball);
    }

    @Test
    public void testSaveBallWhenIdIsNotNullAndEntityDoesNotExist() {
        // Given
        Ball ball = new Ball();
        ball.setId(1L);

        when(entityManager.find(Ball.class, 1L)).thenReturn(null);

        // When
        ballRepositoryImpl.saveBall(ball);

        // Then
        verify(entityManager, times(1)).persist(ball);
        verify(entityManager, times(0)).merge(ball);
    }
}
