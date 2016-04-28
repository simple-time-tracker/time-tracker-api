package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.project.Project;
import com.dovydasvenckus.timetracker.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TimeEntryService {

    @Autowired
    TimeEntryRepository timeEntryRepository;

    @Autowired
    ProjectRepository projectRepository;


    TimeEntry createTimeEntry(Long projectId, String description){
        Project project = projectRepository.findOne(projectId);
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setStartDate(LocalDateTime.now());
        timeEntry.setDescription(description);
        timeEntry.setProject(project);

        timeEntryRepository.save(timeEntry);

        return timeEntry;
    }
}
