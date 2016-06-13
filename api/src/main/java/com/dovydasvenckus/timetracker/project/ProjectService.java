package com.dovydasvenckus.timetracker.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    Project create(ProjectDTO projectDTO) {
        Project project = new Project();
        copyProperties(projectDTO, project);
        project.setDateCreated(LocalDateTime.now());

        projectRepository.save(project);

        return project;
    }
}
