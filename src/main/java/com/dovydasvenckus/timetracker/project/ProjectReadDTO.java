package com.dovydasvenckus.timetracker.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectReadDTO {
    private Long id;

    private String name;

    private boolean isArchived;

    private LocalDateTime dateCreated;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long timeSpentInMilliseconds;

    public ProjectReadDTO(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.dateCreated = project.getDateCreated();
        this.isArchived = project.isArchived();
    }

    public ProjectReadDTO(Project project, long timeSpentInMilliseconds) {
        this(project);
        this.timeSpentInMilliseconds = timeSpentInMilliseconds;
    }
}
