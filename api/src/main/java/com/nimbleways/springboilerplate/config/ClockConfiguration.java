package com.nimbleways.springboilerplate.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfiguration {

    /**
     * Injected everywhere instead of calling LocalDate.now() directly, so that the
     * season and expiry rules can be tested on exact dates.
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
