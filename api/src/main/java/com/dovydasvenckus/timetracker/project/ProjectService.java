package com.dovydasvenckus.timetracker.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    List<ProjectReadDTO> findAllProjects() {
        return projectRepository.findAll().stream()
                .sorted(comparing(Project::getName))
                .map(ProjectReadDTO::new)
                .collect(toList());
    }

    Optional<ProjectReadDTO> findProject(Long id) {
        return projectRepository
                .findOne(id)
                .map(ProjectReadDTO::new);
    }

    Optional<Project> create(ProjectWriteDTO projectWriteDTO) {
        Optional<Project> projectInDb = projectRepository.findByName(projectWriteDTO.getName());

        if (!projectInDb.isPresent()) {
            Project project = new Project();
            project.setName(projectWriteDTO.getName());
            project.setDateCreated(LocalDateTime.now());

            projectRepository.save(project);

            return Optional.of(project);
        } else return Optional.empty();
    }
}
