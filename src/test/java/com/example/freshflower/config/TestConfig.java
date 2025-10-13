package com.example.freshflower.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(basePackages = "com.example.freshflower")
public class TestConfig {
    // Remove the passwordEncoder bean since it's already defined in SecurityConfig
    // The main application beans will be used instead
}