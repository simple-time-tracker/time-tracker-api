package com.dovydasvenckus.timetracker.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    List<ProjectDTO> findAllProjects() {
        return projectRepository.findAll().stream()
                .sorted(comparing(Project::getName))
                .map(ProjectDTO::new)
                .collect(toList());
    }

    Optional<ProjectDTO> findProject(Long id) {
        return projectRepository
                .findOne(id)
                .map(ProjectDTO::new);
    }

    Optional<Project> create(ProjectDTO projectDTO) {
        Optional<Project> projectInDb = projectRepository.findByName(projectDTO.getName());

        if (!projectInDb.isPresent()) {
            Project project = new Project();
            copyProperties(projectDTO, project);
            project.setDateCreated(LocalDateTime.now());

            projectRepository.save(project);

            return Optional.of(project);
        } else return Optional.empty();
    }
}
