package com.dovydasvenckus.timetracker.entry;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TimeEntryRepository extends CrudRepository<TimeEntry, Long> {

    List<TimeEntry> findAll();
}
