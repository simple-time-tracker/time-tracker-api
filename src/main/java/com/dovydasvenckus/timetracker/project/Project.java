package com.dovydasvenckus.timetracker.project;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class Project {

    private Long id;

    private String name;

    private boolean archived = false;

    private LocalDateTime dateCreated;

    private LocalDateTime dateModified;

    private UUID createdBy;

    private UUID modifiedBy;
}
