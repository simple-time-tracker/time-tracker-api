package com.dovydasvenckus.timetracker.project;

import java.time.LocalDateTime;

public class ProjectDTO {
    private Long id;

    private String name;

    private LocalDateTime dateCreated;

    ProjectDTO() {
    }

    public ProjectDTO(Project project) {
        id = project.getId();
        name = project.getName();
        dateCreated = project.getDateCreated();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }
}
