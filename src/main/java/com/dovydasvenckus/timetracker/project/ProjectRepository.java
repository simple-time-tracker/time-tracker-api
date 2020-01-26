package com.dovydasvenckus.timetracker.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    Optional<Project> findByIdAndUserId(Long id, UUID userId);

    Optional<Project> findByNameAndUserId(String name, UUID userId);

    List<Project> findAllByUserId(UUID userId, Sort sort);

    Page<Project> findAllByUserId(UUID userId, Pageable pageable);

    List<Project> findByUserIdAndArchivedFalse(UUID userId, Sort sort);
}
