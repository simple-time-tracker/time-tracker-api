package com.dovydasvenckus.timetracker.project;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectDTO {
    private Long id;

    private String name;

    private LocalDateTime dateCreated;

    ProjectDTO() {}

    public ProjectDTO(Project project) {
        id = project.getId();
        name = project.getName();
        dateCreated = project.getDateCreated();
    }
}
