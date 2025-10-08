package com.codehunter.hotelbooking;


import org.springframework.boot.SpringApplication;

public class TestContainerApplication {
    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(TestContainerConfig.class)
                .run(args);
    }
}
