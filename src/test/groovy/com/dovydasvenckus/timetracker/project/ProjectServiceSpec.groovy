package com.dovydasvenckus.timetracker.project

import com.dovydasvenckus.timetracker.TestDatabaseConfig
import com.dovydasvenckus.timetracker.core.rest.exception.ForbiddenException
import com.dovydasvenckus.timetracker.data.ProjectCreator
import com.dovydasvenckus.timetracker.entry.TimeEntry
import com.dovydasvenckus.timetracker.entry.TimeEntryRepository
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

    @Autowired
    private ProjectCreator projectCreator

    private UUID userId = UUID.randomUUID()


    def 'should return active projects sorted by name ignoring case'() {
        given:
            Project firstProject = projectCreator.createProject("ma", userId)
            Project secondProject = projectCreator.createProject("MB", userId)
            Project archivedProject = projectCreator.createProject("MB", userId, true)

            projectRepository.save(firstProject)
            projectRepository.save(secondProject)
            projectRepository.save(archivedProject)

        when:
            List<ProjectReadDTO> result = projectService.findAllActiveProjects(userId)

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
            Project project = projectCreator.createProject("My project", userId)
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
            ProjectReadDTO result = projectService.getProjectWithTimeSummary(project.id, userId).get()

        then:
            result.id == project.id
            result.name == project.name
            result.timeSpentInMilliseconds == 4380000
    }

    def 'should not include archived projects, when querying for active summaries'() {
        given:
            Project firstProject = projectCreator.createProject("Active 1", userId)
            Project secondProject = projectCreator.createProject("Active 2", userId)
            Project archivedProject = projectCreator.createProject("Archived", userId, true)

            projectRepository.save(firstProject)
            projectRepository.save(secondProject)
            projectRepository.save(archivedProject)

        when:
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, userId)

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
            Project activeProject = projectCreator.createProject("Active", userId)
            Project firstArchivedProject = projectCreator.createProject("Archived 1", userId, true)
            Project secondArchivedProject = projectCreator.createProject("Archived 2", userId, true)

            projectRepository.save(activeProject)
            projectRepository.save(firstArchivedProject)
            projectRepository.save(secondArchivedProject)

        when:
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, true, userId)

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
            Project project = projectCreator.createProject("My project", userId)
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
            ProjectReadDTO result = projectService.getProjectWithTimeSummary(project.id, userId).get()

        then:
            result.id == project.id
            result.name == project.name
            result.timeSpentInMilliseconds == 780000
    }

    def 'should return projects summaries sorted by name ignoring case'() {
        given:
            Project firstProject = projectCreator.createProject("ma", userId)
            Project secondProject = projectCreator.createProject("Mb", userId)

            projectRepository.save(firstProject)
            projectRepository.save(secondProject)

        when:
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, userId)

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
            Project project = projectCreator.createProject("Project", userId)

            projectRepository.save(project)

        when:
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, userId)

        then:
            result.totalElements == 1
            result.content[0].timeSpentInMilliseconds == 0
            result.content[0].name == project.name
    }

    def 'should return correct amount of milliseconds spent per project'() {
        given:
            Project firstProject = projectCreator.createProject("First project", userId)
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
            Project secondProject = projectCreator.createProject("First project", userId)
            TimeEntry secondProjectEntry = createTimeEntry(
                    "Second task project task",
                    secondProject,
                    LocalDateTime.of(2020, 1, 15, 21, 10, 15),
                    LocalDateTime.of(2020, 1, 15, 21, 10, 45)
            )
            projectRepository.save(secondProject)
            timeEntryRepository.save(secondProjectEntry)

        when:
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, userId)

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
            Project project = projectCreator.createProject("First project", userId)
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
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, userId)

        then:
            result.totalElements == 1
            result.content[0].timeSpentInMilliseconds == 3600000
            result.content[0].name == project.name

    }

    def 'should return time tracked without adding currently being tracked project'() {
        given:
            Project firstProject = projectCreator.createProject("First project", userId)
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
            Page<ProjectReadDTO> result = projectService.findAllProjectsWithSummaries(0, 5, false, userId)

        then:
            result.totalElements == 1
            result.content[0].timeSpentInMilliseconds == 780000
            result.content[0].name == firstProject.name
    }

    def 'should not allow to set page size bigger that 20'() {
        expect:
            projectService.findAllProjectsWithSummaries(0, 21, false, userId).pageable.pageSize == 20
    }

    def 'should allow set page size to one'() {
        expect:
            projectService.findAllProjectsWithSummaries(0, 1, false, userId).pageable.pageSize == 1
    }

    def 'should use default page size, if it less than one'() {
        expect:
            projectService.findAllProjectsWithSummaries(0, 0, false, userId).pageable.pageSize == 20
    }

    def 'should archive project'() {
        given:
            Project activeProject = projectCreator.createProject("Active", userId)
            projectRepository.save(activeProject)
        when:
            projectService.archiveProject(activeProject.id, userId)

        then:
            projectRepository.findById(activeProject.id).get().archived
    }

    def 'should update date modified, when archiving'() {
        given:
            Project activeProject = projectCreator.createProject("Active", userId)
            projectRepository.save(activeProject)
        when:
            projectService.archiveProject(activeProject.id, userId)

        then:
            projectRepository.findById(activeProject.id).get().dateModified.isAfter(activeProject.dateModified)
    }

    def 'should restore project from archive state'() {
        given:
            Project archivedProject = projectCreator.createProject("Archived", userId)
            projectRepository.save(archivedProject)
        when:
            projectService.restoreProject(archivedProject.id, userId)

        then:
            !projectRepository.findById(archivedProject.id).get().archived
    }
    def 'should update date modified, when restoring archived project'() {
        given:
            Project archivedProject = projectCreator.createProject("Archived", userId)
            projectRepository.save(archivedProject)
        when:
            projectService.restoreProject(archivedProject.id, userId)

        then:
            projectRepository.findById(archivedProject.id).get().dateModified.isAfter(archivedProject.dateModified)
    }


    def 'should throw forbidden exception if project has different user id'() {
        given:
            UUID differentUser = UUID.randomUUID()
            Project project = projectCreator.createProject("First project", differentUser)
            projectRepository.save(project)
        when:
            projectService.updateProject(project.getId(), new ProjectWriteDTO("new name"), userId)

        then:
            thrown(ForbiddenException)
    }

    def 'should update project, when project created by same user'() {
        given:
            Project project = projectCreator.createProject("First project", userId)
            projectRepository.save(project)
        when:
            projectService.updateProject(project.id, new ProjectWriteDTO("new name"), userId)

        then:
            Project updatedProject = projectRepository.findById(project.id).get()
            updatedProject.name == "new name"
            updatedProject.dateModified.isAfter(project.dateModified)
            updatedProject.modifiedBy == userId
    }

    def 'should return empty project, when there is no project to update'() {
        expect:
            projectService.updateProject(-1, new ProjectWriteDTO("new name"), userId).isEmpty()
    }

    private TimeEntry createTimeEntry(String text, Project project, LocalDateTime startDate, LocalDateTime stopDate) {
        TimeEntry timeEntry = new TimeEntry(
                description: text,
                startDate: startDate,
                endDate: stopDate,
                createdBy: userId
        )
        project.addTimeEntry(timeEntry)
        return timeEntry
    }

}
