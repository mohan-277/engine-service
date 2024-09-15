package com.sbear.gameengineservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

//@SpringBootApplication
//@EntityScan(basePackages = "com.sbear.gameengineservice")
@SpringBootApplication()
public class GameEngineServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameEngineServiceApplication.class, args);
    }

}
