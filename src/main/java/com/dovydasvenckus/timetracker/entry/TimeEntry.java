package com.dovydasvenckus.timetracker.entry;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class TimeEntry {

    private Long id;

    private long projectId;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean deleted = false;

    private UUID createdBy;

    TimeEntry() {
    }

    TimeEntry(TimeEntryDTO timeEntryDTO, UUID createdBy) {
        this.description = timeEntryDTO.getDescription();
        this.projectId = timeEntryDTO.getProjectId();
        this.startDate = timeEntryDTO.getStartDate();
        this.endDate = timeEntryDTO.getEndDate();
        this.createdBy = createdBy;
    }

}
