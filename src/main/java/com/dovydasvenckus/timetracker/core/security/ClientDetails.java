package com.dovydasvenckus.timetracker.core.security;

import lombok.Value;

import java.util.UUID;

@Value
public class ClientDetails {
    private UUID id;
    private String username;
}
