package com.dovydasvenckus.timetracker.project;

import lombok.Data;

@Data
public class ProjectWriteDTO {
    private String name;

    private ProjectWriteDTO(){}

    public ProjectWriteDTO(String name) {
        this.name = name;
    }
}
