package com.dovydasvenckus.timetracker.core.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JwtService {

    public UUID getUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
