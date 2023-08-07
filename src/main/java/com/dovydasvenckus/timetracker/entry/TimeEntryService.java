package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.core.date.clock.DateTimeService;
import com.dovydasvenckus.timetracker.core.pagination.PageSizeResolver;
import com.dovydasvenckus.timetracker.project.Project;
import com.dovydasvenckus.timetracker.project.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        int actualPageSize = pageSizeResolver.resolvePageSize(pageSize);
        long totalCountOfUserEntries = timeEntryRepository.countEntries(userId, false);
        List<TimeEntryDTO> entries = timeEntryRepository
                .findAllByDeleted(
                        userId,
                        false,
                        actualPageSize,
                        page * actualPageSize
                ).stream()
                .map(TimeEntryDTO::new)
            .collect(Collectors.toList());
        Pageable pageRequest = PageRequest.of(page, actualPageSize);

        return new PageImpl<>(entries, pageRequest, totalCountOfUserEntries);
    }

    @Transactional(readOnly = true)
    public Page<TimeEntryDTO> findAllByProject(long projectId, int page, int pageSize, UUID userId) {
        long totalCountOfUserEntries = timeEntryRepository.countEntriesByProject(projectId, userId);
        List<TimeEntryDTO> entries = timeEntryRepository
                .findAllByProjectPage(
                        projectId,
                        userId,
                        pageSize,
                        page * pageSize
                ).stream()
                .map(TimeEntryDTO::new)
                .collect(Collectors.toList());

        Pageable pageRequest = PageRequest.of(page, pageSize);

        return new PageImpl<>(entries, pageRequest, totalCountOfUserEntries);
    }

    @Transactional
    public TimeEntry create(TimeEntryDTO timeEntryDTO, UUID userId) {
        TimeEntry timeEntry = new TimeEntry(timeEntryDTO, userId);
        timeEntryRepository.insert(timeEntry);

        return timeEntry;
    }

    @Transactional
    public void stop(TimeEntryDTO timeEntryDTO, UUID userId) {
        Optional<TimeEntry> updatedEntry = timeEntryRepository.findByIdAndCreatedBy(timeEntryDTO.getId(), userId)
            .map(timeEntry -> {
                timeEntry.setEndDate(dateTimeService.now());
                return timeEntry;
            });
        updatedEntry.ifPresent(timeEntryRepository::update);
    }

    @Transactional
    public void delete(Long id, UUID userId) {
        Optional<TimeEntry> updatedTimeEntry = timeEntryRepository.findByIdAndCreatedBy(id, userId)
            .map(timeEntry -> {
                timeEntry.setDeleted(true);
                return timeEntry;
            });
        updatedTimeEntry.ifPresent(timeEntryRepository::update);
    }

    Optional<TimeEntryDTO> findCurrentlyActive(UUID userId) {
        return timeEntryRepository.findCurrentlyActive(userId)
                .map(TimeEntryDTO::new);
    }

    @Transactional
    public TimeEntry startTracking(Long projectId, String description, UUID userId) {
        Project project = projectRepository.findByIdAndCreatedBy(projectId, userId)
            //TODO #66 fix implement exception mapping to http response
            .orElseThrow(() -> new RuntimeException("Project not found"));

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setStartDate(dateTimeService.now());
        timeEntry.setDescription(description);
        timeEntry.setProjectId(project.getId());
        timeEntry.setCreatedBy(userId);

        timeEntryRepository.insert(timeEntry);

        return timeEntry;
    }
}
