package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.helper.date.clock.DateTimeService;
import com.dovydasvenckus.timetracker.helper.security.ClientDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.core.Context;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class ProjectService {

    private final DateTimeService dateTimeService;

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(DateTimeService dateTimeService, ProjectRepository projectRepository) {
        this.dateTimeService = dateTimeService;
        this.projectRepository = projectRepository;
    }

    List<ProjectReadDTO> findAllProjects(ClientDetails clientDetails) {
        return projectRepository.findAllByUserIdOrderByName(clientDetails.getId()).stream()
                .map(ProjectReadDTO::new)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectReadDTO> findAllProjectsWithSummaries(ClientDetails clientDetails) {
        return projectRepository.findAllByUserIdOrderByName(clientDetails.getId()).stream()
                .map(this::mapToSummary)
                .collect(toList());
    }

    private ProjectReadDTO mapToSummary(Project project) {
        long durationInMilliseconds = project.getTimeEntries().stream()
                .filter(timeEntry -> timeEntry.getEndDate() != null)
                .map(timeEntry -> Duration.between(timeEntry.getStartDate(), timeEntry.getEndDate()).toMillis())
                .reduce(0L, Long::sum);
        return new ProjectReadDTO(project, durationInMilliseconds);
    }

    List<ProjectReadDTO> findAllActiveProjects(ClientDetails clientDetails) {
        return projectRepository.findByUserIdAndArchivedFalseOrderByName(clientDetails.getId()).stream()
                .map(ProjectReadDTO::new)
                .collect(toList());
    }

    Optional<ProjectReadDTO> findProject(Long id, ClientDetails clientDetails) {
        return projectRepository
                .findByIdAndUserId(id, clientDetails.getId())
                .map(ProjectReadDTO::new);
    }

    @Transactional
    public Optional<Project> create(ProjectWriteDTO projectWriteDTO, @Context ClientDetails clientDetails) {
        Optional<Project> projectInDb = projectRepository.findByNameAndUserId(
                projectWriteDTO.getName(),
                clientDetails.getId()
        );

        if (projectInDb.isEmpty()) {
            Project project = new Project();
            project.setName(projectWriteDTO.getName());
            project.setDateCreated(dateTimeService.now());
            project.setUserId(clientDetails.getId());

            projectRepository.save(project);

            return Optional.of(project);
        }

        return Optional.empty();
    }

    @Transactional
    public boolean archiveProject(long projectId, ClientDetails clientDetails) {
        Optional<Project> projectInDb = projectRepository.findByIdAndUserId(projectId, clientDetails.getId());

        return projectInDb.map(project -> {
            project.setArchived(true);
            return true;
        }).orElse(false);
    }
}
