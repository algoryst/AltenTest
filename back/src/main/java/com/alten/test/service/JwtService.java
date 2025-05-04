package com.alten.test.service;

import com.alten.test.security.JWTUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JwtService {

    private final JWTUtil jwtUtil;

    public JwtService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token manquant ou invalide");
        }
        return header.substring(7); // remove "Bearer "
    }

    public String validateToken(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (jwtUtil.isTokenExpired(token)) {
                System.out.println("Token expiré");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expiré");
            }
            return jwtUtil.extractEmail(token);
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Token invalide");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalide");
        }
    }

    public void validateAdmin(HttpServletRequest request) {
        String email = validateToken(request);
        if (!"admin@admin.com".equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès réservé à l'administrateur");
        }
    }
}

