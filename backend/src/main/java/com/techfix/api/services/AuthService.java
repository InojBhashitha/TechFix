package com.techfix.api.services;

import com.techfix.api.dto.AuthResponse;
import com.techfix.api.dto.LoginRequest;
import com.techfix.api.dto.RegisterRequest;
import com.techfix.api.entities.User;
import com.techfix.api.enums.UserRole;
import com.techfix.api.repositories.UserRepository;
import com.techfix.api.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("An account with this email already exists");
        }

        // Create new user
        User user = new User(
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()),
                UserRole.CUSTOMER
        );

        user = userRepository.save(user);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getId()
        );
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getId()
        );
    }

    public User getProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
