package com.dovydasvenckus.timetracker.entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TimeEntryRepository extends CrudRepository<TimeEntry, Long>, TimeEntryRepositoryCustom {
    Optional<TimeEntry> findByIdAndUserId(long id, UUID userId);

    @Query("SELECT te "
            + "FROM TimeEntry te "
            + "WHERE te.userId = :userId AND deleted = :deleted "
            + "ORDER BY te.startDate DESC")
    Page<TimeEntry> findAllByDeleted(@Param("userId") UUID userId,
                                     @Param("deleted") boolean deleted,
                                     Pageable pageable
    );
}
