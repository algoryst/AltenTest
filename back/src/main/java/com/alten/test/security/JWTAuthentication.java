package com.alten.test.security;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class JWTAuthentication extends AbstractAuthenticationToken {

    private final String email;

    public JWTAuthentication(String email) {
        super(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));  // Default role USER
        this.email = email;
        setAuthenticated(true);
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }
}

