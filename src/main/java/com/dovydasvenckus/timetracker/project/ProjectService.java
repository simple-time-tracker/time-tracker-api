package com.dovydasvenckus.timetracker.project;

import static java.util.stream.Collectors.toList;

import com.dovydasvenckus.timetracker.core.date.clock.DateTimeService;
import com.dovydasvenckus.timetracker.core.pagination.PageSizeResolver;
import com.dovydasvenckus.timetracker.core.rest.exception.ForbiddenException;
import com.dovydasvenckus.timetracker.core.security.IsSameUserId;
import com.dovydasvenckus.timetracker.entry.TimeEntryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final DateTimeService dateTimeService;

    private final ProjectRepository projectRepository;

    private final TimeEntryRepository timeEntryRepository;

    private final PageSizeResolver pageSizeResolver;

    public ProjectService(DateTimeService dateTimeService,
                          ProjectRepository projectRepository,
                          TimeEntryRepository timeEntryRepository,
                          PageSizeResolver pageSizeResolver) {
        this.dateTimeService = dateTimeService;
        this.projectRepository = projectRepository;
        this.timeEntryRepository = timeEntryRepository;
        this.pageSizeResolver = pageSizeResolver;
    }

    @Transactional(readOnly = true)
    public Page<ProjectReadDTO> findAllProjectsWithSummaries(int page,
                                                             int pageSize,
                                                             boolean isArchived,
                                                             UUID userId) {
        int actualPageSize = pageSizeResolver.resolvePageSize(pageSize);
        long totalCount = projectRepository.countProjectsByCreatedByAndAndArchiveStatus(userId, isArchived);
        List<ProjectReadDTO> projectsPage = projectRepository.findAllByCreatedByAndArchived(
                userId,
                isArchived,
                actualPageSize,
                page * actualPageSize
        );

        Pageable pageRequest = PageRequest.of(page, actualPageSize);
        return new PageImpl<>(projectsPage, pageRequest, totalCount);
    }

    List<ProjectReadDTO> findAllActiveProjects(UUID userId) {
        return projectRepository.findByCreatedByAndArchivedFalse(userId)
                .stream()
                .map(ProjectReadDTO::new)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProjectReadDTO> getProjectWithTimeSummary(Long id, UUID userId) {
        return projectRepository
                .findProjectSummaryById(id, userId);
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

            projectRepository.insert(project);

            return Optional.of(project)
                    .map(ProjectReadDTO::new);
        }

        return Optional.empty();
    }

    @Transactional
    public Optional<ProjectReadDTO> updateProject(long projectId,
                                                  ProjectWriteDTO updateRequest,
                                                  UUID userId) {
        Optional<Project> updateProject = findById(projectId, userId)
            .map(project -> {
                project.setName(updateRequest.getName());
                project.setDateModified(dateTimeService.now());
                project.setModifiedBy(userId);
                return project;
            });

        updateProject.ifPresent(projectRepository::update);

        return updateProject.map(ProjectReadDTO::new);
    }

    @Transactional
    public boolean archiveProject(long projectId, UUID userId) {
        Optional<Project> projectInDb = projectRepository.findByIdAndCreatedBy(projectId, userId);

        Project updatedProject = projectInDb.map(project -> {
            project.setArchived(true);
            project.setDateModified(dateTimeService.now());
            return project;
        }).orElseThrow(() -> new RuntimeException("Project not found"));

        projectRepository.update(updatedProject);
        return true;
    }

    @Transactional
    public boolean restoreProject(long projectId, UUID userId) {
        Optional<Project> updatedProject = projectRepository.findByIdAndCreatedBy(projectId, userId)
            .map(project -> {
            project.setArchived(false);
            project.setDateModified(dateTimeService.now());
            return project;
        });

        updatedProject.ifPresent(projectRepository::update);
        return updatedProject.isPresent();
    }

    private void validateIfProjectBelongsToSameUser(Project project, UUID userId) {
        if (!IsSameUserId.getInstance().test(project.getCreatedBy(), userId)) {
            throw new ForbiddenException();
        }
    }
}
