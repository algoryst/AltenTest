package com.alten.test.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final String SECRET_KEY = "z^x&i@gz%_f2=%-=y*q5(pr+n9c!6udqbjqbu$hif1jxs1mtui"; // déplacer vers application.properties pour etre paramétré
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    // Generate a JWT token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 3600)) // very long expiry time
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract email from the JWT token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract claims (used in token validation)
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validate if the token is expired
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }
}

