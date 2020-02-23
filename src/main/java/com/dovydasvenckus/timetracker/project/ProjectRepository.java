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

    Optional<Project> findByIdAndUserId(long id, UUID userId);

    Optional<Project> findByNameAndUserId(String name, UUID userId);

    Page<Project> findAllByUserIdAndArchived(UUID userId, boolean isArchived, Pageable pageable);

    List<Project> findByUserIdAndArchivedFalse(UUID userId, Sort sort);
}
