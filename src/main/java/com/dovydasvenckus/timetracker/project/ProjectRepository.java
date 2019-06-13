package com.dovydasvenckus.timetracker.project;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    Optional<Project> findById(Long id);

    Optional<Project> findByName(String name);

    List<Project> findAllByOrderByName();

    List<Project> findByArchivedFalseOrderByName();
}
