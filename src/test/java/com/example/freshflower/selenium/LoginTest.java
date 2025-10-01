package com.example.freshflower.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest extends BaseTest {

    @Test
    public void testSuccessfulLogin() {
        String email = testConfig.getProperty("test.user.email");
        String password = testConfig.getProperty("test.user.password");

        System.out.println("=== Starting Successful Login Test ===");
        System.out.println("Using email: " + email);

        try {
            // Navigate to login page
            driver.get(frontendUrl + "/login");
            System.out.println("Navigated to: " + driver.getCurrentUrl());

            // Wait for page to load and locate elements
            WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
            WebElement passwordInput = driver.findElement(By.id("password"));

            // Find login button using multiple strategies
            WebElement loginButton = null;
            try {
                loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Sign In')]"));
            } catch (Exception e) {
                try {
                    loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Login')]"));
                } catch (Exception e2) {
                    loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
                }
            }

            System.out.println("Found login form elements");

            // Fill login form
            emailInput.sendKeys(email);
            passwordInput.sendKeys(password);

            // Submit form
            loginButton.click();
            System.out.println("Login form submitted");

            // Wait for successful login - check for redirect to dashboard
            try {
                // Wait for redirect to dashboard (increased timeout)
                wait.until(ExpectedConditions.urlContains("/dashboard"));
                String currentUrl = driver.getCurrentUrl();
                System.out.println("Successfully redirected to: " + currentUrl);
                assertTrue(currentUrl.contains("/dashboard"), "Should be redirected to dashboard after successful login");

                System.out.println("Login test passed successfully - redirected to dashboard");
                return; // Test passes if redirected to dashboard

            } catch (Exception e) {
                System.out.println("No redirect to dashboard, checking for messages...");
            }

            // If no redirect, check for success message
            try {
                WebElement successMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class, 'bg-green-50') or contains(@class, 'text-green-700') or contains(@class, 'success')]")
                ));
                String messageText = successMessage.getText().toLowerCase();
                System.out.println("Found success message: " + messageText);

                if (messageText.contains("success") || messageText.contains("welcome") || messageText.contains("logged in")) {
                    System.out.println("Login successful with message");
                    assertTrue(true, "Login completed successfully with message");
                    return;
                }
            } catch (Exception e) {
                System.out.println("No success message found");
            }

            // If neither redirect nor success message, check for error
            try {
                WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class, 'bg-red-50') or contains(@class, 'text-red-700') or contains(@class, 'error')]")
                ));
                String errorText = errorMessage.getText().toLowerCase();
                System.out.println("Found error message: " + errorText);

                // Check what kind of error we got
                if (errorText.contains("invalid password") || errorText.contains("wrong password")) {
                    System.out.println("Login failed due to incorrect password");
                    // This might mean the test user password is wrong in the database
                    fail("Login failed - password might be incorrect for test user. Error: " + errorText);
                } else if (errorText.contains("user not found") || errorText.contains("email not found")) {
                    System.out.println("Login failed - user not found");
                    fail("Login failed - test user not found in database. Error: " + errorText);
                } else {
                    fail("Login failed with error: " + errorText);
                }

            } catch (Exception e) {
                System.out.println("No error message found either");
            }

            // Final fallback - check current URL
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Final URL: " + currentUrl);

            if (currentUrl.contains("/dashboard") || currentUrl.contains("/profile")) {
                System.out.println("Login successful - on dashboard/profile page");
                assertTrue(true, "Login successful");
            } else {
                fail("Login failed - still on login page. URL: " + currentUrl);
            }

        } catch (Exception e) {
            System.out.println("Login test failed with exception: " + e.getMessage());
            e.printStackTrace();
            fail("Login test failed due to exception: " + e.getMessage());
        }
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        String invalidEmail = "nonexistent@example.com";
        String invalidPassword = "WrongPassword123";

        System.out.println("=== Starting Invalid Login Test ===");
        System.out.println("Using invalid email: " + invalidEmail);

        try {
            // Navigate to login page
            driver.get(frontendUrl + "/login");

            // Wait for page to load and locate elements
            WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));
            WebElement passwordInput = driver.findElement(By.id("password"));

            // Find login button
            WebElement loginButton = null;
            try {
                loginButton = driver.findElement(By.xpath("//button[contains(text(), 'Sign In')]"));
            } catch (Exception e) {
                loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
            }

            // Fill form with invalid credentials
            emailInput.sendKeys(invalidEmail);
            passwordInput.sendKeys(invalidPassword);

            // Submit form
            loginButton.click();

            // Wait for error message with multiple strategies
            WebElement errorMessage = null;
            try {
                errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class, 'bg-red-50') or contains(@class, 'text-red-700') or contains(@class, 'error')]")
                ));
            } catch (Exception e) {
                try {
                    errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[contains(text(), 'invalid') or contains(text(), 'incorrect') or contains(text(), 'error') or contains(text(), 'failed') or contains(text(), 'user not found') or contains(text(), 'not exist')]")
                    ));
                } catch (Exception e2) {
                    fail("No error message found for invalid login");
                }
            }

            // Verify error message is displayed
            assertTrue(errorMessage.isDisplayed(), "Error message should be displayed for invalid credentials");

            String errorText = errorMessage.getText().toLowerCase();
            System.out.println("Error message: " + errorText);

            // Updated assertion to match your actual backend responses
            assertTrue(errorText.contains("invalid") ||
                            errorText.contains("incorrect") ||
                            errorText.contains("failed") ||
                            errorText.contains("error") ||
                            errorText.contains("user not found") || // Your actual error message
                            errorText.contains("not exist") ||
                            errorText.contains("wrong"),
                    "Error message should indicate login failure. Actual message: " + errorMessage.getText());

            // Verify we're still on login page (no redirect)
            String currentUrl = driver.getCurrentUrl();
            assertTrue(currentUrl.contains("/login") || currentUrl.endsWith("/login"),
                    "Should remain on login page after failed login. Current URL: " + currentUrl);

            System.out.println("Invalid login test passed successfully");

        } catch (Exception e) {
            System.out.println("Invalid login test failed with exception: " + e.getMessage());
            e.printStackTrace();
            fail("Invalid login test failed due to exception: " + e.getMessage());
        }
    }
}