package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.core.date.clock.DateTimeService;
import com.dovydasvenckus.timetracker.core.pagination.PageSizeResolver;
import com.dovydasvenckus.timetracker.core.security.ClientDetails;
import com.dovydasvenckus.timetracker.core.security.IsSameUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.ForbiddenException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ProjectService {

    private final DateTimeService dateTimeService;

    private final ProjectRepository projectRepository;

    private final PageSizeResolver pageSizeResolver;

    private final Sort.Order defaultSortOrder;

    public ProjectService(DateTimeService dateTimeService,
                          ProjectRepository projectRepository,
                          PageSizeResolver pageSizeResolver) {
        this.dateTimeService = dateTimeService;
        this.projectRepository = projectRepository;
        this.pageSizeResolver = pageSizeResolver;
        this.defaultSortOrder = new Sort.Order(Sort.Direction.ASC, "name").ignoreCase();
    }

    @Transactional(readOnly = true)
    public Page<ProjectReadDTO> findAllProjectsWithSummaries(int page,
                                                             int pageSize,
                                                             boolean isArchived,
                                                             ClientDetails clientDetails) {
        PageRequest pageRequest = PageRequest.of(
                page,
                pageSizeResolver.resolvePageSize(pageSize),
                Sort.by(defaultSortOrder)
        );
        Page<Project> projectsPage = projectRepository.findAllByCreatedByAndArchived(
                clientDetails.getId(),
                isArchived,
                pageRequest
        );

        return transformProjectsPageToSummariesPage(pageRequest, projectsPage);
    }

    List<ProjectReadDTO> findAllActiveProjects(ClientDetails clientDetails) {
        return projectRepository.findByCreatedByAndArchivedFalse(clientDetails.getId(), Sort.by(defaultSortOrder))
                .stream()
                .map(ProjectReadDTO::new)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProjectReadDTO> getProjectWithTimeSummary(Long id, ClientDetails clientDetails) {
        return projectRepository
                .findByIdAndCreatedBy(id, clientDetails.getId())
                .map(this::mapToSummary);
    }

    private Optional<Project> findById(long id, ClientDetails clientDetails) throws ForbiddenException {
        Optional<Project> foundProject = projectRepository.findById(id);
        foundProject.ifPresent(project -> validateIfProjectBelongsToSameUser(project, clientDetails));

        return foundProject;
    }

    @Transactional
    public Optional<ProjectReadDTO> create(ProjectWriteDTO projectWriteDTO, ClientDetails clientDetails) {
        Optional<Project> projectInDb = projectRepository.findByNameAndCreatedBy(
                projectWriteDTO.getName(),
                clientDetails.getId()
        );

        if (projectInDb.isEmpty()) {
            Project project = new Project();
            project.setName(projectWriteDTO.getName());
            project.setDateCreated(dateTimeService.now());
            project.setCreatedBy(clientDetails.getId());

            projectRepository.save(project);

            return Optional.of(project)
                    .map(ProjectReadDTO::new);
        }

        return Optional.empty();
    }

    @Transactional
    public Optional<ProjectReadDTO> updateProject(long projectId,
                                                  ProjectWriteDTO updateRequest,
                                                  ClientDetails clientDetails) {
        return findById(projectId, clientDetails)
                .map(project -> {
                    project.setName(updateRequest.getName());
                    project.setDateModified(dateTimeService.now());
                    project.setModifiedBy(clientDetails.getId());
                    return project;
                }).map(ProjectReadDTO::new);
    }

    @Transactional
    public boolean archiveProject(long projectId, ClientDetails clientDetails) {
        Optional<Project> projectInDb = projectRepository.findByIdAndCreatedBy(projectId, clientDetails.getId());

        return projectInDb.map(project -> {
            project.setArchived(true);
            project.setDateModified(dateTimeService.now());
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean restoreProject(long projectId, ClientDetails clientDetails) {
        Optional<Project> projectInDb = projectRepository.findByIdAndCreatedBy(projectId, clientDetails.getId());

        return projectInDb.map(project -> {
            project.setArchived(false);
            project.setDateModified(dateTimeService.now());
            return true;
        }).orElse(false);
    }

    private Page<ProjectReadDTO> transformProjectsPageToSummariesPage(PageRequest pageRequest,
                                                                      Page<Project> projectsPage) {
        List<ProjectReadDTO> projectSummaries = projectsPage.stream()
                .map(this::mapToSummary)
                .collect(Collectors.toList());

        return new PageImpl<>(projectSummaries, pageRequest, projectsPage.getTotalElements());
    }

    private ProjectReadDTO mapToSummary(Project project) {
        long durationInMilliseconds = project.getTimeEntries().stream()
                .filter(timeEntry -> timeEntry.getEndDate() != null)
                .filter(timeEntry -> !timeEntry.isDeleted())
                .map(timeEntry -> Duration.between(timeEntry.getStartDate(), timeEntry.getEndDate()).toMillis())
                .reduce(0L, Long::sum);
        return new ProjectReadDTO(project, durationInMilliseconds);
    }

    private void validateIfProjectBelongsToSameUser(Project project, ClientDetails clientDetails) {
        if (!IsSameUserId.getInstance().test(project.getCreatedBy(), clientDetails)) {
            throw new ForbiddenException();
        }
    }
}
