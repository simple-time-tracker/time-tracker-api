package com.dovydasvenckus.timetracker.project

import com.dovydasvenckus.timetracker.integration.RestIntegrationSpec
import org.springframework.boot.test.TestRestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class ProjectRestSpec extends RestIntegrationSpec {
    RestTemplate restTemplate = new TestRestTemplate()

    def "should create project"() {
        given:
            URI uri = restTemplate.postForLocation(serviceURI("projects"), new ProjectWriteDTO("Project"));

        when:
            ResponseEntity<ProjectReadDTO> responseEntity = restTemplate.getForEntity(uri, ProjectReadDTO);

        then:
            responseEntity.getBody().getName() == "Project"
            responseEntity.getBody().getId()
    }
}
