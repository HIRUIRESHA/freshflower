package com.example.freshflower.service;

import com.example.freshflower.model.User;
import com.example.freshflower.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Unit Tests")
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User validUser;
    private User existingUser;

    @BeforeEach
    void setUp() {
        validUser = new User("test@example.com", "password123", "John Doe");
        existingUser = new User("existing@example.com", "encodedPassword", "Jane Smith");
        existingUser.setId(1L);
    }

    @Test
    @DisplayName("✅ Should register user successfully with valid data")
    void registerUser_WithValidData_ShouldSaveUserWithEncodedPassword() {
        // Arrange
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(1L);
            return userToSave;
        });

        // Act
        User result = userService.registerUser(validUser);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals("John Doe", result.getFullName());

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(validUser);
    }

    @Test
    @DisplayName("❌ Should throw exception when registering user with null email")
    void registerUser_WithNullEmail_ShouldThrowException() {
        // Arrange
        validUser.setEmail(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(validUser));

        assertEquals("Email cannot be null or empty", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("❌ Should throw exception when registering user with invalid email format")
    void registerUser_WithInvalidEmailFormat_ShouldThrowException() {
        // Arrange
        validUser.setEmail("invalid-email");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(validUser));

        assertEquals("Invalid email format", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("❌ Should throw exception when registering user with short password")
    void registerUser_WithShortPassword_ShouldThrowException() {
        // Arrange
        validUser.setPassword("short");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(validUser));

        assertEquals("Password must be at least 8 characters long", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("❌ Should throw exception when registering null user")
    void registerUser_WithNullUser_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(null));

        assertEquals("User cannot be null", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("✅ Should login user successfully with valid credentials")
    void login_WithValidCredentials_ShouldReturnUser() throws Exception {
        // Arrange
        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("correctPassword", "encodedPassword"))
                .thenReturn(true);

        // Act
        User result = userService.login("existing@example.com", "correctPassword");

        // Assert
        assertNotNull(result);
        assertEquals("existing@example.com", result.getEmail());
        verify(userRepository).findByEmail("existing@example.com");
        verify(passwordEncoder).matches("correctPassword", "encodedPassword");
    }

    @Test
    @DisplayName("❌ Should throw exception when login with non-existent email")
    void login_WithNonExistentEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> userService.login("nonexistent@example.com", "password"));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("❌ Should throw exception when login with incorrect password")
    void login_WithIncorrectPassword_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> userService.login("existing@example.com", "wrongPassword"));

        assertEquals("Invalid password", exception.getMessage());
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    }

    @Test
    @DisplayName("✅ Should check if email exists")
    void emailExists_ShouldReturnTrueWhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        // Act & Assert
        assertTrue(userService.emailExists("existing@example.com"));
        assertFalse(userService.emailExists("new@example.com"));

        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository).existsByEmail("new@example.com");
    }

    @Test
    @DisplayName("✅ Should find user by email")
    void findByEmail_ShouldReturnUserWhenExists() {
        // Arrange
        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // Act
        User foundUser = userService.findByEmail("existing@example.com");
        User notFoundUser = userService.findByEmail("nonexistent@example.com");

        // Assert
        assertNotNull(foundUser);
        assertEquals("existing@example.com", foundUser.getEmail());
        assertNull(notFoundUser);
    }
}
