package com.dovydasvenckus.timetracker.entry;

import java.util.Optional;

interface TimeEntryRepositoryCustom {

    Optional<TimeEntry> findCurrentlyActive();
}
