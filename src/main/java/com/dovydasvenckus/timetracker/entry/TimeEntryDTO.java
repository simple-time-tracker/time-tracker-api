package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.project.ProjectReadDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeEntryDTO {
    private Long id;

    private ProjectReadDTO project;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private TimeEntryDTO() {
    }

    TimeEntryDTO(TimeEntry timeEntry) {
        id = timeEntry.getId();
        project = new ProjectReadDTO(timeEntry.getProject());
        description = timeEntry.getDescription();
        startDate = timeEntry.getStartDate();
        endDate = timeEntry.getEndDate();
    }
}
