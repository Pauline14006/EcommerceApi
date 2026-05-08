package com.ws101.gallamora.torres.EcommerceApi.controller;

import com.ws101.gallamora.torres.EcommerceApi.dto.RegisterUserDto;
import com.ws101.gallamora.torres.EcommerceApi.model.User;
import com.ws101.gallamora.torres.EcommerceApi.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Handles authentication-related endpoints (register).
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterUserDto dto) {
        if (userRepository.findByUsername(dto.username()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}