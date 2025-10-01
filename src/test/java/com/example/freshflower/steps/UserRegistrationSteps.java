package com.example.freshflower.steps;

import com.example.freshflower.model.User;
import com.example.freshflower.repository.UserRepository;
import com.example.freshflower.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class UserRegistrationSteps {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Exception registrationException;
    private User registrationResult;

    @Before
    public void setUp() {
        testUser = null;
        registrationException = null;
        registrationResult = null;
    }

    @Given("the user registration system is ready")
    public void the_user_registration_system_is_ready() {
        assertNotNull(userService);
    }

    @Given("no user exists with email {string}")
    public void no_user_exists_with_email(String email) {
        // Delete user if exists
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
        System.out.println("Ensured no user exists with email: " + email);
    }

    @Given("a user already exists with email {string}")
    public void a_user_already_exists_with_email(String email) {
        // Create a user only if it doesn't exist
        if (!userRepository.findByEmail(email).isPresent()) {
            User existingUser = new User(email, "ExistingPassword123", "Existing User");
            userRepository.save(existingUser);
        }
        System.out.println("Ensured a user exists with email: " + email);
    }

    @When("I register with email {string}, password {string}, and full name {string}")
    public void i_register_with_credentials(String email, String password, String fullName) {
        testUser = new User(email, password, fullName);
        try {
            registrationResult = userService.registerUser(testUser);
        } catch (Exception e) {
            registrationException = e;
        }
    }

    @When("I try to register with the same email {string}")
    public void i_try_to_register_with_the_same_email(String email) {
        testUser = new User(email, "SomePassword123", "Test User");
        try {
            registrationResult = userService.registerUser(testUser);
        } catch (Exception e) {
            registrationException = e;
        }
    }

    @Then("the registration should be successful")
    public void the_registration_should_be_successful() {
        assertNull(registrationException, "No exception should be thrown");
        assertNotNull(registrationResult, "Registration should return a user");
    }

    @Then("the registration should fail with error {string}")
    public void the_registration_should_fail_with_error(String expectedError) {
        assertNotNull(registrationException, "An exception should be thrown");
        assertTrue(registrationException.getMessage().contains(expectedError),
                "Error should contain: " + expectedError);
    }
}
