package com.tutorial.uprit.controller;

import com.tutorial.uprit.dto.AuthResponse;
import com.tutorial.uprit.dto.LoginRequest;
import com.tutorial.uprit.dto.RegisterRequest;
import com.tutorial.uprit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController — handles user registration and login.
 * Endpoints are PUBLIC (no JWT required).
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     *
     * Request body:
     * {
     *   "name": "Ugesh",
     *   "email": "ugesh@uprit.com",
     *   "password": "password123",
     *   "department": "CSE",
     *   "year": 3
     * }
     *
     * Response: { "token": "...", "email": "...", "name": "...", "role": "USER" }
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /api/auth/login
     *
     * Request body:
     * {
     *   "email": "ugesh@uprit.com",
     *   "password": "password123"
     * }
     *
     * Response: { "token": "...", "email": "...", "name": "...", "role": "USER" }
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
