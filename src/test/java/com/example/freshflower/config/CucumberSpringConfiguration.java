package com.example.freshflower.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest
@ContextConfiguration(classes = TestConfig.class)
@ActiveProfiles("test") // <-- add this line
public class CucumberSpringConfiguration {
    // This is the ONLY configuration class
}
