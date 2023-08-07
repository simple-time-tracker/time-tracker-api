package com.dovydasvenckus.timetracker.data

import com.dovydasvenckus.timetracker.core.date.clock.DateTimeService

import com.dovydasvenckus.timetracker.project.Project
import com.dovydasvenckus.timetracker.project.ProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.LocalDateTime

@Component
class ProjectCreator {
    @Autowired
    private DateTimeService dateService
    @Autowired
    private ProjectRepository projectRepository;

    Project createProject(String name, UUID user, boolean archived = false) {
        LocalDateTime currentDate = dateService.now();
        Project project = new Project(
                name: name,
                dateCreated: currentDate,
                dateModified: currentDate,
                createdBy: user,
                modifiedBy: user,
                archived: archived
        )
        projectRepository.insert(project)

        return project
    }
}
