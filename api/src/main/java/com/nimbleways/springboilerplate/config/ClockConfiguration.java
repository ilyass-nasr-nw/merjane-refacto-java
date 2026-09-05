package com.nimbleways.springboilerplate.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfiguration {

    // Injected rather than calling LocalDate.now() directly, so the season and
    // expiry rules can be tested on fixed dates.
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
