package com.dovydasvenckus.timetracker.entry;

import java.util.Optional;
import java.util.UUID;

interface TimeEntryRepositoryCustom {

    Optional<TimeEntry> findCurrentlyActive(UUID userId);
}
