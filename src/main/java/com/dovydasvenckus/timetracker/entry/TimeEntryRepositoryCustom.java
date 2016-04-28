package com.dovydasvenckus.timetracker.entry;

interface TimeEntryRepositoryCustom {
    TimeEntry findCurrentlyActive();
}
