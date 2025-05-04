package com.alten.test.controller;


import com.alten.test.model.User;
import com.alten.test.repository.UserRepository;
import com.alten.test.security.JWTUtil;
import com.alten.test.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final JwtService jwtService;

    public AccountController(UserRepository userRepository, JWTUtil jwtUtil, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<User> createAccount(@RequestBody User user) throws IOException {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        user.setPassword(user.getPassword()); // Hash password in a real app
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/token")
    public ResponseEntity<String> generateToken(@RequestBody User credentials) throws IOException {
        Optional<User> userOptional = userRepository.findByEmail(credentials.getEmail());
        if(userOptional.isEmpty()) {
            System.out.println("not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var user = userOptional.get();
        if (!user.getPassword().equals(credentials.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(token);
    }
}

