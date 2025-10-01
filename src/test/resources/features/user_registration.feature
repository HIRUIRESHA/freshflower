Feature: User Registration
  As a new customer
  I want to register for an account
  So that I can make purchases on the Fresh Flower website

  Scenario: Successful registration with valid information
    Given the user registration system is ready
    And no user exists with email "john.doe@example.com"
    When I register with email "john.doe@example.com", password "SecurePass123", and full name "John Doe"
    Then the registration should be successful

  Scenario: Registration fails with invalid email format
    Given the user registration system is ready
    When I register with email "invalid-email", password "SecurePass123", and full name "John Doe"
    Then the registration should fail with error "Invalid email format"

  Scenario: Registration fails with short password
    Given the user registration system is ready
    When I register with email "test@example.com", password "123", and full name "John Doe"
    Then the registration should fail with error "Password must be at least 8 characters long"

  Scenario: Registration fails with empty full name
    Given the user registration system is ready
    When I register with email "test@example.com", password "SecurePass123", and full name ""
    Then the registration should fail with error "Full name cannot be null or empty"