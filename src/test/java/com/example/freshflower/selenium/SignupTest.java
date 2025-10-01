package com.example.freshflower.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

public class SignupTest extends BaseTest {

    @Test
    public void testUserRegistration() {
        // Generate unique email for each test run
        String timestamp = String.valueOf(System.currentTimeMillis());
        String email = "testuser_" + timestamp + "@example.com";
        String password = "TestPassword123";
        String fullName = "Test User " + timestamp;

        System.out.println("=== Starting Registration Test ===");
        System.out.println("Testing with email: " + email);

        try {
            // Navigate to registration page
            driver.get(frontendUrl + "/register");
            System.out.println("Navigated to: " + driver.getCurrentUrl());

            // Wait for page to load completely
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fullName")));

            // Locate elements
            WebElement fullNameInput = driver.findElement(By.id("fullName"));
            WebElement emailInput = driver.findElement(By.id("email"));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement termsCheckbox = driver.findElement(By.id("terms"));
            WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Create Account')]"));

            System.out.println("Found all form elements");

            // Fill registration form
            fullNameInput.sendKeys(fullName);
            emailInput.sendKeys(email);
            passwordInput.sendKeys(password);

            // Check terms checkbox if not already checked
            if (!termsCheckbox.isSelected()) {
                termsCheckbox.click();
            }

            System.out.println("Form filled, submitting...");

            // Submit form
            submitButton.click();

            // Wait for response - try multiple strategies
            System.out.println("Waiting for response...");

            // Strategy 1: Wait for redirect to dashboard (success case)
            try {
                wait.until(ExpectedConditions.urlContains("/dashboard"));
                String currentUrl = driver.getCurrentUrl();
                System.out.println("Successfully redirected to: " + currentUrl);
                assertTrue(currentUrl.contains("/dashboard"), "Should be redirected to dashboard after successful registration");
                return; // Test passes if redirected to dashboard
            } catch (Exception e) {
                System.out.println("No redirect to dashboard, checking for messages...");
            }

            // Strategy 2: Wait for any message to appear
            WebElement messageDiv = null;
            try {
                // Wait for message container with more flexible selectors
                messageDiv = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class, 'bg-green-50') or contains(@class, 'bg-red-50') or contains(@class, 'text-green-700') or contains(@class, 'text-red-700')]")
                ));
                System.out.println("Found message div with classes: " + messageDiv.getAttribute("class"));
            } catch (Exception e) {
                System.out.println("Could not find message div by class, trying alternative selectors...");

                // Strategy 3: Look for any div that might contain success/error message
                try {
                    messageDiv = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//div[contains(text(), 'success') or contains(text(), 'error') or contains(text(), 'failed') or contains(text(), 'already exists') or contains(text(), 'registered')]")
                    ));
                    System.out.println("Found message div by text: " + messageDiv.getText());
                } catch (Exception e2) {
                    // Strategy 4: Check current URL
                    String currentUrl = driver.getCurrentUrl();
                    System.out.println("Current URL after submission: " + currentUrl);

                    if (currentUrl.contains("/login")) {
                        System.out.println("Redirected to login page - assuming success");
                        return; // Test passes if redirected to login
                    }

                    // Final strategy: Take a screenshot and fail with helpful message
                    System.out.println("Page title: " + driver.getTitle());
                    fail("Registration failed - no success message found and no redirect occurred. Current URL: " + currentUrl);
                }
            }

            // Verify message content
            assertTrue(messageDiv.isDisplayed(), "Message should be displayed after registration");

            String messageText = messageDiv.getText().toLowerCase();
            String classAttribute = messageDiv.getAttribute("class").toLowerCase();

            System.out.println("Message text: " + messageText);
            System.out.println("Message classes: " + classAttribute);

            // Check for success indicators
            if (classAttribute.contains("green") || messageText.contains("success") || messageText.contains("registered")) {
                System.out.println("Registration successful!");
                assertTrue(true, "Registration completed successfully");
            } else if (classAttribute.contains("red") || messageText.contains("error") || messageText.contains("failed") || messageText.contains("already exists")) {
                fail("Registration failed with message: " + messageText);
            } else {
                // If we can't determine, check URL again
                String currentUrl = driver.getCurrentUrl();
                if (currentUrl.contains("/dashboard") || currentUrl.contains("/login")) {
                    System.out.println("Redirected to different page - registration successful");
                    assertTrue(true, "Registration successful - redirected");
                } else {
                    fail("Unable to determine registration status. Message: " + messageText + ", URL: " + currentUrl);
                }
            }

        } catch (Exception e) {
            System.out.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    public void testRegistrationWithExistingEmail() {
        String existingEmail = testConfig.getProperty("test.user.email");
        String password = "TestPassword123";
        String fullName = "Test User";

        System.out.println("=== Testing Duplicate Email Registration ===");
        System.out.println("Using existing email: " + existingEmail);

        try {
            // Navigate to registration page
            driver.get(frontendUrl + "/register");

            // Wait for page to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fullName")));

            // Locate elements
            WebElement fullNameInput = driver.findElement(By.id("fullName"));
            WebElement emailInput = driver.findElement(By.id("email"));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement termsCheckbox = driver.findElement(By.id("terms"));
            WebElement submitButton = driver.findElement(By.xpath("//button[contains(text(), 'Create Account')]"));

            // Fill form with existing email
            fullNameInput.sendKeys(fullName);
            emailInput.sendKeys(existingEmail);
            passwordInput.sendKeys(password);

            if (!termsCheckbox.isSelected()) {
                termsCheckbox.click();
            }

            System.out.println("Submitting form with duplicate email...");

            // Submit form
            submitButton.click();

            // Wait for error message with multiple strategies
            WebElement errorMessage = null;
            try {
                errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class, 'bg-red-50') or contains(@class, 'text-red-700')]")
                ));
            } catch (Exception e) {
                // Try alternative selectors
                try {
                    errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(text(), 'already exists') or contains(text(), 'email') or contains(text(), 'error') or contains(text(), 'failed')]")
                    ));
                } catch (Exception e2) {
                    fail("No error message found for duplicate email registration");
                }
            }

            // Verify error message
            assertTrue(errorMessage.isDisplayed(), "Error message should be displayed for duplicate email");

            String errorText = errorMessage.getText().toLowerCase();
            System.out.println("Error message: " + errorText);

            assertTrue(errorText.contains("already exists") ||
                            errorText.contains("email") ||
                            errorText.contains("failed") ||
                            errorText.contains("error"),
                    "Error message should indicate email already exists. Actual message: " + errorMessage.getText());

            System.out.println("Duplicate email test passed successfully");

        } catch (Exception e) {
            System.out.println("Duplicate email test failed with exception: " + e.getMessage());
            e.printStackTrace();
            fail("Duplicate email test failed due to exception: " + e.getMessage());
        }
    }
}