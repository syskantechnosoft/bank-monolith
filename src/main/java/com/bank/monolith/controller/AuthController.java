package com.bank.monolith.controller;

import com.bank.monolith.common.dto.ApiResponse;
import com.bank.monolith.common.dto.AuthResponse;
import com.bank.monolith.common.dto.LoginRequest;
import com.bank.monolith.common.dto.RegisterRequest;
import com.bank.monolith.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse responseData = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(responseData, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse responseData = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(responseData, "Login successful"));
    }
}
