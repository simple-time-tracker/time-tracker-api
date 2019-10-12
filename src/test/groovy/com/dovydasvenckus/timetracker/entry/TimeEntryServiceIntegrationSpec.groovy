package com.dovydasvenckus.timetracker.entry

import com.dovydasvenckus.timetracker.TestDatabaseConfig
import com.dovydasvenckus.timetracker.project.Project
import com.dovydasvenckus.timetracker.project.ProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification

import java.time.LocalDateTime

@Testcontainers
@SpringBootTest(classes = TestDatabaseConfig)
class TimeEntryServiceIntegrationSpec extends Specification {

    @Autowired
    ProjectRepository projectRepository

    @Autowired
    TimeEntryRepository timeEntryRepository

    @Autowired
    TimeEntryService timeEntryService

    def 'should mark as deleted'() {
        given:
            Project project = new Project(name: "Project", dateCreated: LocalDateTime.now())
            TimeEntry timeEntry = createTimeEntry("Entry to delete", project)
            project.addTimeEntry(timeEntry)

            projectRepository.save(project)
            timeEntryRepository.save(timeEntry)

        when:
            timeEntryService.delete(timeEntry.id)

        then:
            timeEntryRepository.findById(timeEntry.id).get().deleted
    }

    def 'should return only not deleted entries'() {
        given:
            Project project = new Project(name: "Project", dateCreated: LocalDateTime.now())
            TimeEntry timeEntry1 = createTimeEntry("Time entry 1", project)

            TimeEntry timeEntry2 = createTimeEntry("Time entry 2", project)
            project.addTimeEntry(timeEntry2)

            projectRepository.save(project)
            timeEntryRepository.save(timeEntry1)
            timeEntryRepository.save(timeEntry2)

        and:
            timeEntryService.delete(timeEntry2.id)

        when:
            List<TimeEntryDTO> result = timeEntryService.findAll(0).getContent()

        then:
            result.size() == 1
            with(result.get(0)) {
                id == timeEntry1.id
                description == timeEntry1.description
                startDate == timeEntry1.startDate
                endDate == timeEntry1.endDate
            }
    }

    private static TimeEntry createTimeEntry(String text, Project project) {
        TimeEntry timeEntry = new TimeEntry(
                description: text,
                startDate: LocalDateTime.now()
        )
        project.addTimeEntry(timeEntry)
        return timeEntry
    }
}
