package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.helper.date.DateTimeService.DateTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Service
public class ProjectService {

    private DateTimeService dateTimeService;

    private ProjectRepository projectRepository;

    @Autowired
    public ProjectService(DateTimeService dateTimeService, ProjectRepository projectRepository) {
        this.dateTimeService = dateTimeService;
        this.projectRepository = projectRepository;
    }

    List<ProjectReadDTO> findAllProjects() {
        return projectRepository.findAll().stream()
                .sorted(comparing(Project::getName))
                .map(ProjectReadDTO::new)
                .collect(toList());
    }

    Optional<ProjectReadDTO> findProject(Long id) {
        return projectRepository
                .findById(id)
                .map(ProjectReadDTO::new);
    }

    Optional<Project> create(ProjectWriteDTO projectWriteDTO) {
        Optional<Project> projectInDb = projectRepository.findByName(projectWriteDTO.getName());

        if (!projectInDb.isPresent()) {
            Project project = new Project();
            project.setName(projectWriteDTO.getName());
            project.setDateCreated(dateTimeService.now());

            projectRepository.save(project);

            return Optional.of(project);
        } else return Optional.empty();
    }

    @Transactional
    public boolean archiveProject(long projectId) {
        Optional<Project> project = projectRepository.findById(projectId);

        if (project.isPresent()) {
            project.get().setArchived(true);
            return true;
        }

        return false;
    }
}
