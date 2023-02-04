package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.core.date.clock.DateTimeService;
import com.dovydasvenckus.timetracker.core.pagination.PageSizeResolver;
import com.dovydasvenckus.timetracker.project.Project;
import com.dovydasvenckus.timetracker.project.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class TimeEntryService {

    private final DateTimeService dateTimeService;

    private final TimeEntryRepository timeEntryRepository;

    private final ProjectRepository projectRepository;

    private final PageSizeResolver pageSizeResolver;

    public TimeEntryService(DateTimeService dateTimeService,
                            TimeEntryRepository timeEntryRepository,
                            ProjectRepository projectRepository,
                            PageSizeResolver pageSizeResolver) {
        this.dateTimeService = dateTimeService;
        this.timeEntryRepository = timeEntryRepository;
        this.projectRepository = projectRepository;
        this.pageSizeResolver = pageSizeResolver;
    }

    @Transactional(readOnly = true)
    Page<TimeEntryDTO> findAll(int page, int pageSize, UUID userId) {
        return timeEntryRepository
                .findAllByDeleted(
                        userId,
                        false,
                        PageRequest.of(page, pageSizeResolver.resolvePageSize(pageSize))
                )
                .map(TimeEntryDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<TimeEntryDTO> findAllByProject(long projectId, int page, int pageSize, UUID userId) {
        return timeEntryRepository
                .findAllByProject(
                        projectId,
                        userId,
                        PageRequest.of(page, pageSizeResolver.resolvePageSize(pageSize))
                )
                .map(TimeEntryDTO::new);
    }

    @Transactional
    public TimeEntry create(TimeEntryDTO timeEntryDTO, UUID userId) {
        timeEntryDTO.setId(null);
        TimeEntry timeEntry;
        timeEntry = new TimeEntry(timeEntryDTO, userId);
        Optional<Project> project = projectRepository.findByIdAndCreatedBy(
                timeEntryDTO.getProject().getId(),
                userId
        );
        project.ifPresent(timeEntry::setProject);
        timeEntryRepository.save(timeEntry);

        return timeEntry;
    }

    @Transactional
    public void stop(TimeEntryDTO timeEntryDTO, UUID userId) {
        timeEntryRepository.findByIdAndCreatedBy(timeEntryDTO.getId(), userId)
                .ifPresent(entry -> entry.setEndDate(dateTimeService.now()));
    }

    @Transactional
    public void delete(Long id, UUID userId) {
        timeEntryRepository.findByIdAndCreatedBy(id, userId)
                .ifPresent(timeEntry -> timeEntry.setDeleted(true));
    }

    Optional<TimeEntryDTO> findCurrentlyActive(UUID userId) {
        return timeEntryRepository.findCurrentlyActive(userId)
                .map(TimeEntryDTO::new);
    }

    @Transactional
    public TimeEntry startTracking(Long projectId, String description, UUID userId) {
        Optional<Project> project = projectRepository.findByIdAndCreatedBy(projectId, userId);
        if (project.isPresent()) {
            TimeEntry timeEntry = new TimeEntry();
            timeEntry.setStartDate(dateTimeService.now());
            timeEntry.setDescription(description);
            timeEntry.setProject(project.get());
            timeEntry.setCreatedBy(userId);

            timeEntryRepository.save(timeEntry);

            return timeEntry;
        }
        return null;
    }
}
