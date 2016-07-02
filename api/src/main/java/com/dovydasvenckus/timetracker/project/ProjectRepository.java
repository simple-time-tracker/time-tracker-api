package com.dovydasvenckus.timetracker.project;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends Repository<Project, Long> {

    Optional<Project> findOne(Long id);

    Optional<Project> findByName(String name);

    List<Project> findAll();

    void save(Project project);
}
