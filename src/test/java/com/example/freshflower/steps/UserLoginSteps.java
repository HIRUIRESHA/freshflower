package com.example.freshflower.steps;

import com.example.freshflower.model.User;
import com.example.freshflower.service.UserService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@Transactional  // ensures DB changes persist for the scenario
public class UserLoginSteps {

    @Autowired
    private UserService userService;

    private String loginEmail;
    private String loginPassword;
    private User loginResult;
    private Exception loginException;

    @Given("the login system is ready")
    public void the_login_system_is_ready() {
        assertNotNull(userService, "UserService should be injected by Spring");
    }

    @Given("a registered user exists with email {string} and password {string}")
    public void a_registered_user_exists_with_credentials(String email, String password) {
        // Only create the user if it doesn't exist
        if (!userService.emailExists(email)) {
            User user = new User(email, password, "Test User");
            userService.registerUser(user);
        }
    }

    @When("I login with email {string} and password {string}")
    public void i_login_with_credentials(String email, String password) {
        this.loginEmail = email;
        this.loginPassword = password;
        try {
            loginResult = userService.login(email, password);
            loginException = null;
        } catch (Exception e) {
            loginException = e;
            loginResult = null;
        }
    }

    @Then("the login should be successful")
    public void the_login_should_be_successful() {
        assertNull(loginException, "Expected no exception but got: " +
                (loginException != null ? loginException.getMessage() : "null"));
        assertNotNull(loginResult, "Login result should not be null");
        assertEquals(loginEmail, loginResult.getEmail(),
                "Logged in user email should match");
    }

    @Then("the login should fail with error {string}")
    public void the_login_should_fail_with_error(String expectedError) {
        assertNotNull(loginException, "An exception should be thrown");
        assertTrue(loginException.getMessage().contains(expectedError),
                "Expected error to contain: '" + expectedError +
                        "' but got: '" + loginException.getMessage() + "'");
    }
}
