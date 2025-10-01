package com.example.freshflower.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Properties testConfig;
    protected String frontendUrl;
    protected String backendUrl;

    @BeforeEach
    public void setUp() throws IOException {
        // Load test configuration
        testConfig = new Properties();
        testConfig.load(new FileInputStream("src/test/resources/test-config.properties"));

        frontendUrl = testConfig.getProperty("frontend.url");
        backendUrl = testConfig.getProperty("backend.url");

        // Setup Chrome driver
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        if ("true".equals(testConfig.getProperty("selenium.headless"))) {
            options.addArguments("--headless");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");

        driver = new ChromeDriver(options);

        // Set up waits with longer timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Maximize window
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}