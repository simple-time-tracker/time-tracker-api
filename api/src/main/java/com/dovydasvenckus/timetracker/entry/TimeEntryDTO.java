package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.project.ProjectDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeEntryDTO {
    private Long id;

    private ProjectDTO project;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Long differenceInMinutes;

    TimeEntryDTO(){}

    TimeEntryDTO(TimeEntry timeEntry) {
        id = timeEntry.getId();
        project = new ProjectDTO(timeEntry.getProject());
        description = timeEntry.getDescription();
        startDate = timeEntry.getStartDate();
        endDate = timeEntry.getEndDate();
    }
}
