package com.dovydasvenckus.timetracker.entry;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TimeEntryDTO {
    private Long id;

    private long projectId;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private TimeEntryDTO() {
    }

    TimeEntryDTO(TimeEntry timeEntry) {
        this.id = timeEntry.getId();
        this.projectId = timeEntry.getProjectId();
        this.description = timeEntry.getDescription();
        this.startDate = timeEntry.getStartDate();
        this.endDate = timeEntry.getEndDate();
    }
}
