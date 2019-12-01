package com.dovydasvenckus.timetracker.config;

import com.dovydasvenckus.timetracker.entry.TimeEntryController;
import com.dovydasvenckus.timetracker.helper.security.ClientDetails;
import com.dovydasvenckus.timetracker.helper.security.CurrentUserResolver;
import com.dovydasvenckus.timetracker.project.ProjectController;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
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
        register(currentUserContext());
        EncodingFilter.enableFor(this, GZipEncoder.class);
        setProperties(Collections.singletonMap("jersey.config.server.response.setStatusOverSendError", true));
    }

    private AbstractBinder currentUserContext() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(CurrentUserResolver.class).to(ClientDetails.class);
            }
        };
    }
}
