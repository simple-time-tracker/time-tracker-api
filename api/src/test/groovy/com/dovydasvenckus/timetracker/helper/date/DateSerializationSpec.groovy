package com.dovydasvenckus.timetracker.helper.date

import com.dovydasvenckus.timetracker.helper.date.clock.DateTimeService
import com.dovydasvenckus.timetracker.helper.date.clock.UTCDateTimeService
import com.dovydasvenckus.timetracker.helper.date.serialization.LocalDateTimeSerializer
import com.fasterxml.jackson.core.JsonGenerator
import spock.lang.Specification

import java.time.LocalDateTime

class DateSerializationSpec extends Specification {
    DateTimeService dateTimeService = new UTCDateTimeService()
    LocalDateTimeSerializer dateSerializer =  new LocalDateTimeSerializer()
    JsonGenerator jsonGenerator = Mock(JsonGenerator)
    String rawJson;

    def setup() {
        jsonGenerator.writeString(_) >> { args -> rawJson = args[0]}
    }

    def "should serialize to UTC string"() {
        given:
            LocalDateTime currentTime = dateTimeService.now()
        when:
            dateSerializer.serialize(currentTime, jsonGenerator, null)
        then:
            rawJson.minus('Z') == currentTime.toString()
    }

}
