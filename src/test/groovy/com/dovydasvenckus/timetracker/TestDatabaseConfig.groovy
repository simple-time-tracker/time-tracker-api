package com.dovydasvenckus.timetracker

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer

import javax.sql.DataSource

@TestConfiguration
class TestDatabaseConfig {

    @Bean
    PostgreSQLContainer postgreSQLContainer() {
        final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer('postgres:11-alpine')
        postgreSQLContainer.start()
        return postgreSQLContainer
    }

    @Bean
    DataSource dataSource(final PostgreSQLContainer testDB) {
        return DataSourceBuilder
                .create()
                .url(testDB.getJdbcUrl())
                .username(testDB.getUsername())
                .password(testDB.getPassword())
                .driverClassName(testDB.getDriverClassName())
                .build()
    }
}
