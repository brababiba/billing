package com.brababiba.billing.service;

import com.brababiba.billing.common.ErrorMessages;
import com.brababiba.billing.dto.auth.AuthResponse;
import com.brababiba.billing.dto.auth.LoginRequest;
import com.brababiba.billing.dto.auth.RegisterRequest;
import com.brababiba.billing.model.User;
import com.brababiba.billing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtService jwtService;

    public void register(RegisterRequest request) {

        User user = new User();

        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setCreatedAt(Instant.now());

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(ErrorMessages.INVALID_EMAIL_OR_PASSWORD));

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new RuntimeException(ErrorMessages.INVALID_EMAIL_OR_PASSWORD);
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}
