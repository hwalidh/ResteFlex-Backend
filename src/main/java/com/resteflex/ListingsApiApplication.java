package com.resteflex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ListingsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ListingsApiApplication.class, args);
    }
}
