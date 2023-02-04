package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.core.date.clock.DateTimeService;
import com.dovydasvenckus.timetracker.core.pagination.PageSizeResolver;
import com.dovydasvenckus.timetracker.core.rest.exception.ForbiddenException;
import com.dovydasvenckus.timetracker.core.security.IsSameUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
                                                             UUID userId) {
        PageRequest pageRequest = PageRequest.of(
                page,
                pageSizeResolver.resolvePageSize(pageSize),
                Sort.by(defaultSortOrder)
        );
        Page<Project> projectsPage = projectRepository.findAllByCreatedByAndArchived(
                userId,
                isArchived,
                pageRequest
        );

        return transformProjectsPageToSummariesPage(pageRequest, projectsPage);
    }

    List<ProjectReadDTO> findAllActiveProjects(UUID userId) {
        return projectRepository.findByCreatedByAndArchivedFalse(userId, Sort.by(defaultSortOrder))
                .stream()
                .map(ProjectReadDTO::new)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProjectReadDTO> getProjectWithTimeSummary(Long id, UUID userId) {
        return projectRepository
                .findByIdAndCreatedBy(id, userId)
                .map(this::mapToSummary);
    }

    private Optional<Project> findById(long id, UUID userId) throws ForbiddenException {
        Optional<Project> foundProject = projectRepository.findById(id);
        foundProject.ifPresent(project -> validateIfProjectBelongsToSameUser(project, userId));

        return foundProject;
    }

    @Transactional
    public Optional<ProjectReadDTO> create(ProjectWriteDTO projectWriteDTO, UUID userId) {
        Optional<Project> projectInDb = projectRepository.findByNameAndCreatedBy(
                projectWriteDTO.getName(),
                userId
        );

        if (projectInDb.isEmpty()) {
            LocalDateTime creationDate = dateTimeService.now();
            Project project = new Project();
            project.setName(projectWriteDTO.getName());
            project.setDateCreated(creationDate);
            project.setDateModified(creationDate);
            project.setCreatedBy(userId);
            project.setModifiedBy(userId);

            projectRepository.save(project);

            return Optional.of(project)
                    .map(ProjectReadDTO::new);
        }

        return Optional.empty();
    }

    @Transactional
    public Optional<ProjectReadDTO> updateProject(long projectId,
                                                  ProjectWriteDTO updateRequest,
                                                  UUID userId) {
        return findById(projectId, userId)
                .map(project -> {
                    project.setName(updateRequest.getName());
                    project.setDateModified(dateTimeService.now());
                    project.setModifiedBy(userId);
                    return project;
                }).map(ProjectReadDTO::new);
    }

    @Transactional
    public boolean archiveProject(long projectId, UUID userId) {
        Optional<Project> projectInDb = projectRepository.findByIdAndCreatedBy(projectId, userId);

        return projectInDb.map(project -> {
            project.setArchived(true);
            project.setDateModified(dateTimeService.now());
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean restoreProject(long projectId, UUID userId) {
        Optional<Project> projectInDb = projectRepository.findByIdAndCreatedBy(projectId, userId);

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

    private void validateIfProjectBelongsToSameUser(Project project, UUID userId) {
        if (!IsSameUserId.getInstance().test(project.getCreatedBy(), userId)) {
            throw new ForbiddenException();
        }
    }
}
