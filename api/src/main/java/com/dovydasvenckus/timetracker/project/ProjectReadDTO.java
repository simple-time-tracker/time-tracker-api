package com.dovydasvenckus.timetracker.project;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectReadDTO {
    private Long id;

    private String name;

    private LocalDateTime dateCreated;

    private ProjectReadDTO() {
    }

    public ProjectReadDTO(Project project) {
        id = project.getId();
        name = project.getName();
        dateCreated = project.getDateCreated();
    }
}
