package com.soulrebel.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableJdbcRepositories
public class Bootstrap {

    public static void main(String[] args) {
        SpringApplication.run (Bootstrap.class, args);
    }

}
