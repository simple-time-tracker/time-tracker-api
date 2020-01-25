package com.dovydasvenckus.timetracker.project

import com.dovydasvenckus.timetracker.TestDatabaseConfig
import com.dovydasvenckus.timetracker.entry.TimeEntry
import com.dovydasvenckus.timetracker.entry.TimeEntryRepository
import com.dovydasvenckus.timetracker.helper.security.ClientDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

import java.time.LocalDateTime

@Testcontainers
@SpringBootTest(classes = TestDatabaseConfig)
class ProjectServiceSpec extends Specification {

    @Autowired
    private ProjectRepository projectRepository

    @Autowired
    private TimeEntryRepository timeEntryRepository

    @Autowired
    private ProjectService projectService

    private ClientDetails user = new ClientDetails(UUID.randomUUID(), 'name')

    def 'should return zero milliseconds tracked for project, when project has no time entries'() {
        given:
            Project project = new Project(name: "Project", dateCreated: LocalDateTime.now(), userId: user.id)

            projectRepository.save(project)

        when:
            List<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(user)

        then:
            result.size() == 1
            result[0].timeSpentInMilliseconds == 0
            result[0].name == project.name
    }

    def 'should return correct amount of milliseconds spent per project'() {
        given:
            Project firstProject = new Project(name: "First project", dateCreated: LocalDateTime.now(), userId: user.id)
            TimeEntry firstEntry = createTimeEntry(
                    "First task",
                    firstProject,
                    LocalDateTime.of(2020, 1, 15, 21, 00),
                    LocalDateTime.of(2020, 1, 15, 21, 13)
            )
            TimeEntry secondEntry = createTimeEntry("Second task",
                    firstProject,
                    LocalDateTime.of(2020, 01, 13, 15, 00),
                    LocalDateTime.of(2020, 01, 13, 16, 00)
            )
            projectRepository.save(firstProject)
            timeEntryRepository.save(firstEntry)
            timeEntryRepository.save(secondEntry)

        and:
            Project secondProject = new Project(name: "First project", dateCreated: LocalDateTime.now(), userId: user.id)
            TimeEntry secondProjectEntry = createTimeEntry(
                    "Second task project task",
                    secondProject,
                    LocalDateTime.of(2020, 1, 15, 21, 10, 15),
                    LocalDateTime.of(2020, 1, 15, 21, 10, 45)
            )
            projectRepository.save(secondProject)
            timeEntryRepository.save(secondProjectEntry)

        when:
            List<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(user)

        then:
            result.size() == 2
            result[0].timeSpentInMilliseconds == 4380000
            result[0].name == firstProject.name

        and:
            result[1].timeSpentInMilliseconds == 30000
            result[1].name == secondProject.name
    }

    def 'should return time tracked without adding currently being tracked project'() {
        given:
            Project firstProject = new Project(name: "First project", dateCreated: LocalDateTime.now(), userId: user.id)
            TimeEntry firstEntry = createTimeEntry(
                    "First task",
                    firstProject,
                    LocalDateTime.of(2020, 1, 15, 21, 00),
                    LocalDateTime.of(2020, 1, 15, 21, 13)
            )
            TimeEntry secondEntry = createTimeEntry("Second task",
                    firstProject,
                    LocalDateTime.now().minusHours(1),
                    null
            )
            projectRepository.save(firstProject)
            timeEntryRepository.save(firstEntry)
            timeEntryRepository.save(secondEntry)

        when:
            List<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(user)

        then:
            result.size() == 1
            result[0].timeSpentInMilliseconds == 780000
            result[0].name == firstProject.name
    }


    private TimeEntry createTimeEntry(String text, Project project, LocalDateTime startDate, LocalDateTime stopDate) {
        TimeEntry timeEntry = new TimeEntry(
                description: text,
                startDate: startDate,
                endDate: stopDate,
                userId: user.id
        )
        project.addTimeEntry(timeEntry)
        return timeEntry
    }

}
