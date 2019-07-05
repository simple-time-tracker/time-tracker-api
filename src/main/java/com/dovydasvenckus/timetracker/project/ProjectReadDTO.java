package com.dovydasvenckus.timetracker.project;

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

    public ProjectReadDTO(Project project) {
        id = project.getId();
        name = project.getName();
        dateCreated = project.getDateCreated();
        isArchived = project.isArchived();
    }
}
