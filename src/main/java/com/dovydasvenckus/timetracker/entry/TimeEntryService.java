package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.helper.date.clock.DateTimeService;
import com.dovydasvenckus.timetracker.project.Project;
import com.dovydasvenckus.timetracker.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class TimeEntryService {

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
    List<TimeEntryDTO> findAll() {
        return timeEntryRepository.findAllByOrderByStartDateDesc().stream()
                .map(TimeEntryDTO::new).collect(toList());
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
        timeEntryRepository.deleteById(id);
    }


    Optional<TimeEntryDTO> findCurrentlyActive() {
        TimeEntry timeEntry = timeEntryRepository.findCurrentlyActive();

        if (timeEntry != null) {
            TimeEntryDTO timeEntryDTO = new TimeEntryDTO(timeEntry);

            return Optional.of(timeEntryDTO);
        }

        return Optional.empty();
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
