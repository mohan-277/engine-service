package com.sbear.gameengineservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExceptionHandler  {

  @ExceptionHandler(PlayerNotFoundException.class)
  public ResponseEntity<?> handlePlayerNotFoundException(PlayerNotFoundException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            ex.getMessage(),
            "Player not found in the database"
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }
}
