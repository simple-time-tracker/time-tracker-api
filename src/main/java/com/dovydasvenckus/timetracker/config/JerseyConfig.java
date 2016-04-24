package com.dovydasvenckus.timetracker.config;

import com.dovydasvenckus.timetracker.project.ProjectController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(ProjectController.class);
    }
}
