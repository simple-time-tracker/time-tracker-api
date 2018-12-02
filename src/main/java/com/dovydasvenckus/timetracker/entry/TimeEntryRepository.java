package com.dovydasvenckus.timetracker.entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface TimeEntryRepository extends CrudRepository<TimeEntry, Long>, TimeEntryRepositoryCustom {
    Page<TimeEntry> findAllByOrderByStartDateDesc(Pageable pageable);

}
