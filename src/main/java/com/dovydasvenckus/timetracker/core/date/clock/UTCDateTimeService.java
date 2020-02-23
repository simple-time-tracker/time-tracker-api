package com.dovydasvenckus.timetracker.core.date.clock;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class UTCDateTimeService implements DateTimeService {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(Clock.systemUTC());
    }
}
