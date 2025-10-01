package com.example.freshflower.controller;

import com.example.freshflower.model.User;
import com.example.freshflower.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // React dev server
public class AuthController {

    @Autowired
    private UserService userService;

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        try {
            if (userService.emailExists(user.getEmail())) {
                response.put("message", "Email already exists");
                return ResponseEntity.badRequest().body(response);
            }

            userService.registerUser(user);
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // Login user
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        Map<String, String> response = new HashMap<>();

        try {
            User existingUser = userService.login(loginRequest.getEmail(), loginRequest.getPassword());

            response.put("message", "Login successful");
            response.put("userId", existingUser.getId().toString());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(401).body(response); // Unauthorized
        }
    }
}
