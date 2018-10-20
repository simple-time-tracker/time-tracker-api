package com.dovydasvenckus.timetracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@Provider
@Consumes("application/json")
@Produces("application/json")
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper = new ObjectMapper();

    public ObjectMapperContextResolver() {
        SimpleModule module = new SimpleModule();

        mapper.registerModule(module);
        mapper.registerModule(createCustomJavaTimeSerializationModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // add JAXB annotation support if required
        mapper.registerModule(new JaxbAnnotationModule());
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }

    private JavaTimeModule createCustomJavaTimeSerializationModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(createUTCLocalDateTimeFormatter()));
        return javaTimeModule;
    }

    private DateTimeFormatter createUTCLocalDateTimeFormatter() {
        return (new DateTimeFormatterBuilder()).parseCaseInsensitive().append(DateTimeFormatter.ISO_LOCAL_DATE_TIME).appendLiteral("Z").toFormatter();
    }

}
