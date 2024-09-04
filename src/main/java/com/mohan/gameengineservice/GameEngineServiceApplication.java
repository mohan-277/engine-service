package com.mohan.gameengineservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.mohan.gameengineservice")
public class GameEngineServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameEngineServiceApplication.class, args);
    }

}
