package com.ecommerce.advance.auth.service;


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

    public String register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
        return jwtUtil.generateToken(username, user.getRole());
    }

    public String login(String username, String password) {
        log.info("Login for user :{}",username);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("User does not exist"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(username, user.getRole());
    }
}
