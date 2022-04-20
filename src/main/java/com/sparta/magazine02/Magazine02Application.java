package com.sparta.magazine02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Magazine02Application {

    public static void main(String[] args) {
        SpringApplication.run(Magazine02Application.class, args);
    }

}
