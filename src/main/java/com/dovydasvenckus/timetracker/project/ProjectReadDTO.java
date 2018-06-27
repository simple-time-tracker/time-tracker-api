package com.dovydasvenckus.timetracker.project;

import com.dovydasvenckus.timetracker.helper.date.serialization.LocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectReadDTO {
    private Long id;

    private String name;
    
    private boolean isArchived;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dateCreated;

    private ProjectReadDTO() {
    }

    public ProjectReadDTO(Project project) {
        id = project.getId();
        name = project.getName();
        dateCreated = project.getDateCreated();
        isArchived = project.isArchived();
    }
}
