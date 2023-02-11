package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.entry.TimeEntry;
import lombok.Data;
import lombok.ToString;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "is_archived")
    private boolean archived = false;

    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated;

    @Column(name = "date_modified", nullable = false)
    private LocalDateTime dateModified;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "modified_by")
    private UUID modifiedBy;

    @OneToMany(mappedBy = "project")
    @ToString.Exclude
    private List<TimeEntry> timeEntries = new ArrayList<>();

    public void addTimeEntry(TimeEntry timeEntry) {
        this.timeEntries.add(timeEntry);
        if (timeEntry.getProject() != this) {
            timeEntry.setProject(this);
        }
    }
}
