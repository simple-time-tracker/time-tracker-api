package com.dovydasvenckus.timetracker.core.security;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiPredicate;

public class IsSameUserId implements BiPredicate<UUID, ClientDetails> {

    @Override
    public boolean test(UUID userId, ClientDetails currentUser) {
        if (currentUser == null || currentUser.getId() == null || userId == null) {
            return false;
        }
        return Objects.equals(userId, currentUser.getId());
    }

    public static IsSameUserId getInstance() {
        return new IsSameUserId();
    }
}
