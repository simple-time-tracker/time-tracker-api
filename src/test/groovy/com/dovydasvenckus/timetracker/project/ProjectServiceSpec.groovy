package com.dovydasvenckus.timetracker.project

import com.dovydasvenckus.timetracker.TestDatabaseConfig
import com.dovydasvenckus.timetracker.entry.TimeEntry
import com.dovydasvenckus.timetracker.entry.TimeEntryRepository
import com.dovydasvenckus.timetracker.helper.security.ClientDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
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


    def 'should return active projects sorted by name ignoring case'() {
        given:
            Project firstProject = new Project(name: "ma", dateCreated: LocalDateTime.now(), userId: user.id)
            Project secondProject = new Project(name: "MB", dateCreated: LocalDateTime.now(), userId: user.id)
            Project archivedProject = new Project(name: "MB", dateCreated: LocalDateTime.now(), userId: user.id, archived: true)

            projectRepository.save(firstProject)
            projectRepository.save(secondProject)
            projectRepository.save(archivedProject)

        when:
            List<ProjectReadDTO> result = projectService.findAllActiveProjects(user)

        then:
            result.size() == 2
            result[0].id == firstProject.id
            result[0].name == 'ma'
        and:
            result[1].id == secondProject.id
            result[1].name == 'MB'
    }

    def 'should find and return project with correct amount tracked'() {
        given:
            Project project = new Project(name: "My project", dateCreated: LocalDateTime.now(), userId: user.id)
            TimeEntry firstEntry = createTimeEntry(
                    "First task",
                    project,
                    LocalDateTime.of(2020, 1, 15, 21, 00),
                    LocalDateTime.of(2020, 1, 15, 21, 13)
            )
            TimeEntry secondEntry = createTimeEntry("Second task",
                    project,
                    LocalDateTime.of(2020, 01, 13, 15, 00),
                    LocalDateTime.of(2020, 01, 13, 16, 00)
            )
            projectRepository.save(project)
            timeEntryRepository.save(firstEntry)
            timeEntryRepository.save(secondEntry)

        when:
            ProjectReadDTO result = projectService.getProjectWithTimeSummary(project.id, user).get()

        then:
            result.id == project.id
            result.name == project.name
            result.timeSpentInMilliseconds == 4380000
    }

    def 'should not include archived projects, when querying for active summaries'() {
        given:
            Project firstProject = new Project(name: "Active 1", dateCreated: LocalDateTime.now(), userId: user.id)
            Project secondProject = new Project(name: "Active 2", dateCreated: LocalDateTime.now(), userId: user.id)
            Project archivedProject = new Project(name: "Archived", dateCreated: LocalDateTime.now(), userId: user.id, archived: true)

            projectRepository.save(firstProject)
            projectRepository.save(secondProject)
            projectRepository.save(archivedProject)

        when:
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, user)

        then:
            result.totalElements == 2
            result.content[0].id == firstProject.id
            result.content[0].name == 'Active 1'
        and:
            result.content[1].id == secondProject.id
            result.content[1].name == 'Active 2'
    }

    def 'should not include active projects, when querying for archived summaries'() {
        given:
            Project activeProject = new Project(name: "Active", dateCreated: LocalDateTime.now(), userId: user.id)
            Project firstArchivedProject = new Project(name: "Archived 1", dateCreated: LocalDateTime.now(), userId: user.id, archived: true)
            Project secondArchivedProject = new Project(name: "Archived 2", dateCreated: LocalDateTime.now(), userId: user.id, archived: true)

            projectRepository.save(activeProject)
            projectRepository.save(firstArchivedProject)
            projectRepository.save(secondArchivedProject)

        when:
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, true, user)

        then:
            result.totalElements == 2
            result.content[0].id == firstArchivedProject.id
            result.content[0].name == 'Archived 1'
        and:
            result.content[1].id == secondArchivedProject.id
            result.content[1].name == 'Archived 2'
    }

    def 'should not include deleted time entries in amount tracked'() {
        given:
            Project project = new Project(name: "My project", dateCreated: LocalDateTime.now(), userId: user.id)
            TimeEntry firstEntry = createTimeEntry(
                    "First task",
                    project,
                    LocalDateTime.of(2020, 1, 15, 21, 00),
                    LocalDateTime.of(2020, 1, 15, 21, 13)
            )
            TimeEntry secondEntry = createTimeEntry("Second task",
                    project,
                    LocalDateTime.of(2020, 01, 13, 15, 00),
                    LocalDateTime.of(2020, 01, 13, 16, 00)
            )
            secondEntry.deleted = true
            projectRepository.save(project)
            timeEntryRepository.save(firstEntry)
            timeEntryRepository.save(secondEntry)

        when:
            ProjectReadDTO result = projectService.getProjectWithTimeSummary(project.id, user).get()

        then:
            result.id == project.id
            result.name == project.name
            result.timeSpentInMilliseconds == 780000
    }


    def 'should return projects summaries sorted by name ignoring case'() {
        given:
            Project firstProject = new Project(name: "ma", dateCreated: LocalDateTime.now(), userId: user.id)
            Project secondProject = new Project(name: "Mb", dateCreated: LocalDateTime.now(), userId: user.id)

            projectRepository.save(firstProject)
            projectRepository.save(secondProject)

        when:
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, user)

        then:
            result.totalElements == 2
            result.content[0].id == firstProject.id
            result.content[0].name == 'ma'
        and:
            result.content[1].id == secondProject.id
            result.content[1].name == 'Mb'
    }


    def 'should return zero milliseconds tracked for project, when project has no time entries'() {
        given:
            Project project = new Project(name: "Project", dateCreated: LocalDateTime.now(), userId: user.id)

            projectRepository.save(project)

        when:
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, user)

        then:
            result.totalElements == 1
            result.content[0].timeSpentInMilliseconds == 0
            result.content[0].name == project.name
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
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, user)

        then:
            result.totalElements == 2
            result.content[0].timeSpentInMilliseconds == 4380000
            result.content[0].name == firstProject.name

        and:
            result.content[1].timeSpentInMilliseconds == 30000
            result.content[1].name == secondProject.name
    }

    def 'should not count deleted time entry'() {
        given:
            Project project = new Project(name: "First project", dateCreated: LocalDateTime.now(), userId: user.id)
            TimeEntry firstEntry = createTimeEntry(
                    "First task",
                    project,
                    LocalDateTime.of(2020, 1, 15, 21, 00),
                    LocalDateTime.of(2020, 1, 15, 21, 13)
            )
            firstEntry.deleted = true

            TimeEntry secondEntry = createTimeEntry("Second task",
                    project,
                    LocalDateTime.of(2020, 01, 13, 15, 00),
                    LocalDateTime.of(2020, 01, 13, 16, 00)
            )
            projectRepository.save(project)
            timeEntryRepository.save(firstEntry)
            timeEntryRepository.save(secondEntry)

        when:
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, user)

        then:
            result.totalElements == 1
            result.content[0].timeSpentInMilliseconds == 3600000
            result.content[0].name == project.name

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
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, user)

        then:
            result.totalElements == 1
            result.content[0].timeSpentInMilliseconds == 780000
            result.content[0].name == firstProject.name
    }

    def 'should not allow to set page size bigger that 20'() {
        expect:
            projectService.findAllProjectsWithSummaries(0, 21, false, user).pageable.pageSize == 20
    }

    def 'should allow set page size to one'() {
        expect:
            projectService.findAllProjectsWithSummaries(0, 1, false, user).pageable.pageSize == 1
    }

    def 'should use default page size, if it less than one'() {
        expect:
            projectService.findAllProjectsWithSummaries(0, 0, false, user).pageable.pageSize == 20
    }

    def 'should archive project'() {
        given:
            Project activeProject = new Project(name: "Active", dateCreated: LocalDateTime.now(), userId: user.id)
            projectRepository.save(activeProject)
        when:
            projectService.archiveProject(activeProject.id, user)

        then:
            projectRepository.findById(activeProject.id).get().archived == true
    }


    def 'should restore project from archive state'() {
        Project archivedProject = new Project(name: "Archived", dateCreated: LocalDateTime.now(), userId: user.id, archived: true)
        projectRepository.save(archivedProject)
        when:
            projectService.restoreProject(archivedProject.id, user)

        then:
            projectRepository.findById(archivedProject.id).get().archived == false
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
