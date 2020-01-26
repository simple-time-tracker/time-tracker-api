package com.dovydasvenckus.timetracker.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    Optional<Project> findByIdAndUserId(Long id, UUID userId);

    Optional<Project> findByNameAndUserId(String name, UUID userId);

    List<Project> findAllByUserIdOrderByName(UUID userId);

    Page<Project> findAllByUserIdOrderByName(UUID userId, Pageable pageable);

    List<Project> findByUserIdAndArchivedFalseOrderByName(UUID userId);
}
