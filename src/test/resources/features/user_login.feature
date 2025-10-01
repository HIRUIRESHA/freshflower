Feature: User Login
  As a registered user
  I want to login to my account
  So that I can access my personalized features

  Scenario: Successful login with correct credentials
    Given the login system is ready
    And a registered user exists with email "user@example.com" and password "Password123"
    When I login with email "user@example.com" and password "Password123"
    Then the login should be successful

  Scenario: Login fails with incorrect password
    Given the login system is ready
    And a registered user exists with email "user@example.com" and password "Password123"
    When I login with email "user@example.com" and password "WrongPassword"
    Then the login should fail with error "Invalid password"

  Scenario: Login fails with non-existent email
    Given the login system is ready
    When I login with email "nonexistent@example.com" and password "Password123"
    Then the login should fail with error "User not found"