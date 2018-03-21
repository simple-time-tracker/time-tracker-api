package com.dovydasvenckus.timetracker.security;

class SecurityConstants {
    static final long EXPIRATION_TIME = 864_000_000;
    static final String TOKEN_PREFIX = "Bearer ";
    static final String HEADER_STRING = "Authorization";
    static final String SECRET = "SecretKeyToGenJWTs";
}
