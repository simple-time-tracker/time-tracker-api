package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.core.date.clock.DateTimeService;
import com.dovydasvenckus.timetracker.core.pagination.PageSizeResolver;
import com.dovydasvenckus.timetracker.core.security.ClientDetails;
import com.dovydasvenckus.timetracker.project.Project;
import com.dovydasvenckus.timetracker.project.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    Page<TimeEntryDTO> findAll(int page, int pageSize, ClientDetails clientDetails) {
        return timeEntryRepository
                .findAllByDeleted(
                        clientDetails.getId(),
                        false,
                        PageRequest.of(page, pageSizeResolver.resolvePageSize(pageSize))
                )
                .map(TimeEntryDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<TimeEntryDTO> findAllByProject(long projectId, int page, int pageSize, ClientDetails clientDetails) {
        return timeEntryRepository
                .findAllByProject(
                        projectId,
                        clientDetails.getId(),
                        PageRequest.of(page, pageSizeResolver.resolvePageSize(pageSize))
                )
                .map(TimeEntryDTO::new);
    }

    @Transactional
    public TimeEntry create(TimeEntryDTO timeEntryDTO, ClientDetails clientDetails) {
        timeEntryDTO.setId(null);
        TimeEntry timeEntry;
        timeEntry = new TimeEntry(timeEntryDTO, clientDetails.getId());
        Optional<Project> project = projectRepository.findByIdAndCreatedBy(
                timeEntryDTO.getProject().getId(),
                clientDetails.getId()
        );
        project.ifPresent(timeEntry::setProject);
        timeEntryRepository.save(timeEntry);

        return timeEntry;
    }

    @Transactional
    public void stop(TimeEntryDTO timeEntryDTO, ClientDetails clientDetails) {
        timeEntryRepository.findByIdAndCreatedBy(timeEntryDTO.getId(), clientDetails.getId())
                .ifPresent(entry -> entry.setEndDate(dateTimeService.now()));
    }

    @Transactional
    public void delete(Long id, ClientDetails clientDetails) {
        timeEntryRepository.findByIdAndCreatedBy(id, clientDetails.getId())
                .ifPresent(timeEntry -> timeEntry.setDeleted(true));
    }

    Optional<TimeEntryDTO> findCurrentlyActive(ClientDetails clientDetails) {
        return timeEntryRepository.findCurrentlyActive(clientDetails.getId())
                .map(TimeEntryDTO::new);
    }

    @Transactional
    public TimeEntry startTracking(Long projectId, String description, ClientDetails clientDetails) {
        Optional<Project> project = projectRepository.findByIdAndCreatedBy(projectId, clientDetails.getId());
        if (project.isPresent()) {
            TimeEntry timeEntry = new TimeEntry();
            timeEntry.setStartDate(dateTimeService.now());
            timeEntry.setDescription(description);
            timeEntry.setProject(project.get());
            timeEntry.setCreatedBy(clientDetails.getId());

            timeEntryRepository.save(timeEntry);

            return timeEntry;
        }
        return null;
    }
}
