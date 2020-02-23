package com.dovydasvenckus.timetracker.entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TimeEntryRepository extends CrudRepository<TimeEntry, Long>, TimeEntryRepositoryCustom {
    Optional<TimeEntry> findByIdAndCreatedBy(long id, UUID userId);

    @Query("SELECT te "
            + "FROM TimeEntry te "
            + "WHERE te.createdBy = :userId AND deleted = :deleted "
            + "ORDER BY te.startDate DESC")
    Page<TimeEntry> findAllByDeleted(@Param("userId") UUID userId,
                                     @Param("deleted") boolean deleted,
                                     Pageable pageable
    );

    @Query("SELECT te "
            + "FROM TimeEntry te "
            + "WHERE te.project.id = :projectId AND te.createdBy = :userId AND te.deleted = false "
            + "ORDER BY te.startDate DESC")

    Page<TimeEntry> findAllByProject(@Param("projectId") long projectId,
                                     @Param("userId") UUID userId,
                                     Pageable pageable
    );
}
