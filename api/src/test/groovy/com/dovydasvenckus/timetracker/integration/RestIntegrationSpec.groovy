package com.dovydasvenckus.timetracker.integration

import com.dovydasvenckus.timetracker.TimeTrackerApplication
import com.dovydasvenckus.timetracker.config.JerseyConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.TestRestTemplate
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Stepwise

@ContextConfiguration(loader = SpringApplicationContextLoader.class,
        classes = [TimeTrackerApplication, JerseyConfig])
@WebIntegrationTest("server.port=9090")
@Stepwise
class RestIntegrationSpec extends Specification {
    @Value('${local.server.port}')
    Integer port

    RestTemplate restTemplate = new TestRestTemplate()

    String getBasePath() { "api/" }

    URI serviceURI(String path = "") {
        new URI("http://localhost:$port/${basePath}${path}")
    }

}
