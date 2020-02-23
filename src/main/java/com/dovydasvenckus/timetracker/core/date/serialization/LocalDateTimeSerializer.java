package com.dovydasvenckus.timetracker.core.date.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime dateTime, JsonGenerator jg,
                          SerializerProvider sp) throws IOException {
        Instant instant = dateTime.atZone(ZoneOffset.UTC).toInstant();
        jg.writeString(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(instant) + "Z");
    }
}