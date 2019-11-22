package com.dovydasvenckus.timetracker.config;

import com.dovydasvenckus.timetracker.entry.TimeEntryController;
import com.dovydasvenckus.timetracker.project.ProjectController;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.EncodingFilter;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(ObjectMapperContextResolver.class);
        register(ProjectController.class);
        register(TimeEntryController.class);
        EncodingFilter.enableFor(this, GZipEncoder.class);
        setProperties(Collections.singletonMap("jersey.config.server.response.setStatusOverSendError", true));
    }
}
