package com.dovydasvenckus.timetracker.core.security;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiPredicate;

public class IsSameUserId implements BiPredicate<UUID, UUID> {

    @Override
    public boolean test(UUID userId, UUID currentUser) {
        if (currentUser == null || userId == null) {
            return false;
        }
        return Objects.equals(userId, currentUser);
    }

    public static IsSameUserId getInstance() {
        return new IsSameUserId();
    }
}
