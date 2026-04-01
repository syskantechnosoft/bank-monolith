package com.bank.monolith.service;

import com.bank.monolith.common.dto.AuthResponse;
import com.bank.monolith.common.dto.LoginRequest;
import com.bank.monolith.common.dto.RegisterRequest;
import com.bank.monolith.domain.entity.User;
import com.bank.monolith.domain.enums.Role;
import com.bank.monolith.domain.repository.UserRepository;
import com.bank.monolith.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterCustomerSuccessfully() {
        RegisterRequest req = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password123")
                .phoneNumber("+123456")
                .build();

        when(userRepository.existsByEmail(req.getEmail())).thenReturn(false);
        when(userRepository.existsByPhoneNumber(req.getPhoneNumber())).thenReturn(false);
        when(passwordEncoder.encode(req.getPassword())).thenReturn("encodedPass");

        User savedUser = User.builder()
                .id(1L)
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.ROLE_CUSTOMER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("mockJwtToken");

        AuthResponse resp = authService.register(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getToken()).isEqualTo("mockJwtToken");
        assertThat(resp.getEmail()).isEqualTo("john@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterRequest req = RegisterRequest.builder()
                .email("john@example.com")
                .build();

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email is already in use");
    }
}
