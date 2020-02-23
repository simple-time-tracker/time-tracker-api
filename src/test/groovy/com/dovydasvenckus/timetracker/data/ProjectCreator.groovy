package com.dovydasvenckus.timetracker.data

import com.dovydasvenckus.timetracker.core.date.clock.DateTimeService
import com.dovydasvenckus.timetracker.core.security.ClientDetails
import com.dovydasvenckus.timetracker.project.Project
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ProjectCreator {
    @Autowired
    private DateTimeService dateService

    Project createProject(String name, ClientDetails user, boolean archived = false) {
        new Project(
                name: name,
                dateCreated: dateService.now(),
                dateModified: dateService.now(),
                createdBy: user.id,
                modifiedBy: user.id,
                archived: archived
        )
    }
}
