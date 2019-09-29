package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.helper.date.clock.DateTimeService;
import com.dovydasvenckus.timetracker.project.Project;
import com.dovydasvenckus.timetracker.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TimeEntryService {

    private static final int PAGE_SIZE = 20;
    private final DateTimeService dateTimeService;

    private final TimeEntryRepository timeEntryRepository;

    private final ProjectRepository projectRepository;

    @Autowired
    public TimeEntryService(DateTimeService dateTimeService,
                            TimeEntryRepository timeEntryRepository,
                            ProjectRepository projectRepository) {
        this.dateTimeService = dateTimeService;
        this.timeEntryRepository = timeEntryRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    Page<TimeEntryDTO> findAll(int page) {
        return timeEntryRepository
                   .findAllByDeletedOrderByStartDateDesc(false, PageRequest.of(page, PAGE_SIZE))
                   .map(TimeEntryDTO::new);
    }

    @Transactional
    public TimeEntry create(TimeEntryDTO timeEntryDTO) {
        timeEntryDTO.setId(null);
        TimeEntry timeEntry;
        timeEntry = new TimeEntry(timeEntryDTO);
        Optional<Project> project = projectRepository.findById(timeEntryDTO.getProject().getId());
        project.ifPresent(timeEntry::setProject);
        timeEntryRepository.save(timeEntry);

        return timeEntry;
    }

    @Transactional
    public void stop(TimeEntryDTO timeEntryDTO) {
        timeEntryRepository.findById(timeEntryDTO.getId())
            .ifPresent(entry -> entry.setEndDate(dateTimeService.now()));
    }

    @Transactional
    public void delete(Long id) {
        timeEntryRepository.findById(id)
            .ifPresent(timeEntry -> timeEntry.setDeleted(true));
    }

    Optional<TimeEntryDTO> findCurrentlyActive() {
        return timeEntryRepository.findCurrentlyActive()
            .map(TimeEntryDTO::new);
    }


    @Transactional
    public TimeEntry createTimeEntry(Long projectId, String description) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent()) {
            TimeEntry timeEntry = new TimeEntry();
            timeEntry.setStartDate(dateTimeService.now());
            timeEntry.setDescription(description);
            timeEntry.setProject(project.get());

            timeEntryRepository.save(timeEntry);

            return timeEntry;
        }
        return null;
    }
}
