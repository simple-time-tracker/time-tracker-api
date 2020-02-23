package com.dovydasvenckus.timetracker.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    Optional<Project> findById(long id);

    Optional<Project> findByIdAndCreatedBy(long id, UUID userId);

    Optional<Project> findByNameAndCreatedBy(String name, UUID userId);

    Page<Project> findAllByCreatedByAndArchived(UUID userId, boolean isArchived, Pageable pageable);

    List<Project> findByCreatedByAndArchivedFalse(UUID userId, Sort sort);
}
