package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.project.Project;
import com.dovydasvenckus.timetracker.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class TimeEntryService {

    @Autowired
    TimeEntryRepository timeEntryRepository;

    @Autowired
    ProjectRepository projectRepository;

    List<TimeEntryDTO> findAll() {
        List<TimeEntryDTO> timeEntries = timeEntryRepository.findAll().stream().
                map(TimeEntryDTO::new).collect(toList());

        return timeEntries;
    }

    TimeEntry create(TimeEntryDTO timeEntryDTO) {
        TimeEntry timeEntry;
        timeEntry = new TimeEntry(timeEntryDTO);
        Project project = projectRepository.findOne(timeEntryDTO.getId());
        timeEntry.setProject(project);
        timeEntryRepository.save(timeEntry);

        return timeEntry;
    }

    TimeEntry update(TimeEntryDTO timeEntryDTO){
        TimeEntry timeEntry = timeEntryRepository.findOne(timeEntryDTO.getId());

        timeEntry.setDescription(timeEntryDTO.getDescription());
        timeEntry.setStartDate(timeEntryDTO.getStartDate());
        timeEntry.setEndDate(timeEntryDTO.getEndDate());
        timeEntryRepository.save(timeEntry);

        return timeEntry;
    }


    Optional<TimeEntryDTO> findCurrentlyActive() {
        TimeEntry timeEntry = timeEntryRepository.findCurrentlyActive();

        if (timeEntry != null) {
            TimeEntryDTO timeEntryDTO = new TimeEntryDTO(timeEntry);

            return Optional.of(timeEntryDTO);
        }

        return Optional.empty();
    }


    TimeEntry createTimeEntry(Long projectId, String description) {
        Project project = projectRepository.findOne(projectId);
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setStartDate(LocalDateTime.now());
        timeEntry.setDescription(description);
        timeEntry.setProject(project);

        timeEntryRepository.save(timeEntry);

        return timeEntry;
    }
}
