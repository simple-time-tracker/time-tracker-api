package com.dovydasvenckus.timetracker.entry

import com.dovydasvenckus.timetracker.TestDatabaseConfig
import com.dovydasvenckus.timetracker.helper.security.ClientDetails
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

    private ClientDetails user = new ClientDetails(UUID.randomUUID(), 'name')

    def 'should mark as deleted'() {
        given:
            Project project = new Project(name: "Project", dateCreated: LocalDateTime.now(), userId: user.id)
            TimeEntry timeEntry = createTimeEntry("Entry to delete", project)
            project.addTimeEntry(timeEntry)

            projectRepository.save(project)
            timeEntryRepository.save(timeEntry)

        when:
            timeEntryService.delete(timeEntry.id, user)

        then:
            timeEntryRepository.findById(timeEntry.id).get().deleted
    }

    def 'should not allow to set page size bigger that 20'() {
        expect:
            timeEntryService.findAll(0, 21, user).pageable.pageSize == 20
    }

    def 'should allow set page size to one'() {
        expect:
            timeEntryService.findAll(0, 1, user).pageable.pageSize == 1
    }

    def 'should use default page size, if it less than one'() {
        expect:
            timeEntryService.findAll(0, 0, user).pageable.pageSize == 20
    }

    def 'should return only not deleted entries'() {
        given:
            Project project = new Project(name: "Project", dateCreated: LocalDateTime.now(), userId: user.id)
            TimeEntry timeEntry1 = createTimeEntry("Time entry 1", project)

            TimeEntry timeEntry2 = createTimeEntry("Time entry 2", project)
            project.addTimeEntry(timeEntry2)

            projectRepository.save(project)
            timeEntryRepository.save(timeEntry1)
            timeEntryRepository.save(timeEntry2)

        and:
            timeEntryService.delete(timeEntry2.id, user)

        when:
            List<TimeEntryDTO> result = timeEntryService.findAll(0, 5, user).getContent()

        then:
            result.size() == 1
            with(result.get(0)) {
                id == timeEntry1.id
                description == timeEntry1.description
                startDate == timeEntry1.startDate
                endDate == timeEntry1.endDate
            }
    }

    def 'should return ordered by start date'() {
        given:
            Project project = new Project(name: "Project", dateCreated: LocalDateTime.now(), userId: user.id)
            TimeEntry timeEntry1 = createTimeEntry("Time entry 1", project)

            TimeEntry timeEntry2 = createTimeEntry("Time entry 2", project)
            LocalDateTime secondEntryStartDate = timeEntry2.getStartDate()
            timeEntry2.setStartDate(secondEntryStartDate.plusSeconds(1))
            project.addTimeEntry(timeEntry2)

            projectRepository.save(project)
            timeEntryRepository.save(timeEntry1)
            timeEntryRepository.save(timeEntry2)

        when:
            List<TimeEntryDTO> result = timeEntryService.findAll(0, 5, user).getContent()

        then:
            result.size() == 2
            result.get(0).id == timeEntry2.id
            result.get(1).id == timeEntry1.id
    }

    private TimeEntry createTimeEntry(String text, Project project) {
        TimeEntry timeEntry = new TimeEntry(
                description: text,
                startDate: LocalDateTime.now(),
                userId: user.id
        )
        project.addTimeEntry(timeEntry)
        return timeEntry
    }
}
