package com.dovydasvenckus.timetracker.config;

import com.dovydasvenckus.timetracker.entry.TimeEntryController;
import com.dovydasvenckus.timetracker.project.ProjectController;
import com.dovydasvenckus.timetracker.user.UserController;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(ObjectMapperContextResolver.class);
        register(ProjectController.class);
        register(TimeEntryController.class);
        register(UserController.class);
        EncodingFilter.enableFor(this, GZipEncoder.class);
    }
}
