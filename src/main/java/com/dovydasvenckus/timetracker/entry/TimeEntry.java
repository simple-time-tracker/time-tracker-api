package com.dovydasvenckus.timetracker.entry;

import com.dovydasvenckus.timetracker.project.Project;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.ToString;

@Entity
@Table(name = "time_entries")
@Data
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "time_entry_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @ToString.Exclude
    private Project project;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_deleted")
    private boolean deleted = false;

    TimeEntry() {
    }

    TimeEntry(TimeEntryDTO timeEntryDTO) {
        id = timeEntryDTO.getId();
        description = timeEntryDTO.getDescription();
        startDate = timeEntryDTO.getStartDate();
        endDate = timeEntryDTO.getEndDate();
    }

}
