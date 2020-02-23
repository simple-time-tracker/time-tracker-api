package com.dovydasvenckus.timetracker.core.security;

import org.glassfish.hk2.api.Factory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserResolver implements Factory<ClientDetails> {
    @Override
    public ClientDetails provide() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return (ClientDetails) authentication.getPrincipal();
    }

    @Override
    public void dispose(ClientDetails instance) {

    }

}
