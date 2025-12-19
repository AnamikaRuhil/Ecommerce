package com.ecommerce.advance.auth.service;


import com.ecommerce.advance.auth.dto.AuthRequest;
import com.ecommerce.advance.auth.dto.AuthResponse;
import com.ecommerce.advance.auth.exception.DataNotFoundException;
import com.ecommerce.advance.auth.model.UserEntity;
import com.ecommerce.advance.auth.repository.UserRepository;
import com.ecommerce.advance.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        String token =  jwtUtil.generateToken(user.getUsername(), user.getRole());

        userRepository.save(user);
        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .token(token)
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(String username, String password) {
        log.info("Login for user :{}",username);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("User does not exist"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(username, user.getRole());
        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .token(token)
                .message("User Login successfully")
                .build();

    }
}
