package com.codehunter.hotelbooking;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {
    @Container
//    static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17.2")
    static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("pgvector/pgvector:0.8.1-pg18-trixie")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");


    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgreSQLContainer() {
        return POSTGRES;
    }

    @Bean
    DynamicPropertyRegistrar dynamicPropertyRegistrar() {
        return registry -> {

            registry.add("spring.datasources.happyrobot.url", POSTGRES::getJdbcUrl);
            registry.add("spring.datasources.happyrobot.username", POSTGRES::getUsername);
            registry.add("spring.datasources.happyrobot.password", POSTGRES::getPassword);
            registry.add("spring.datasources.happyrobot.driverClassName", POSTGRES::getDriverClassName);
        };
    }

    static {
        POSTGRES.start();
    }
}
