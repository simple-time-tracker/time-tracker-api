package com.dovydasvenckus.timetracker.config;

import com.dovydasvenckus.timetracker.helper.date.LocalDateTimeDeserializer;
import com.dovydasvenckus.timetracker.helper.date.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.time.LocalDateTime;

@Provider
@Consumes("application/json")
@Produces("application/json")
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    final ObjectMapper mapper = new ObjectMapper();

    public ObjectMapperContextResolver() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        mapper.registerModule(module);
        // add JAXB annotation support if required
        mapper.registerModule(new JaxbAnnotationModule());
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }

}
