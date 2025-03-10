package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Autowired
    @Qualifier("JdbcCursorJob")
    private Job jdbcCursorJob;

//    @Bean
//    @Primary
    public Job testJob() {
        return jdbcCursorJob;
    }
}
