package com.nimbleways.springboilerplate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Time-related application configuration.
 * <p>
 * Exposes a {@link Clock} bean to make date/time operations deterministic and testable
 * (e.g. by injecting a fixed clock in unit tests).
 * </p>
 */
@Configuration
public class TimeConfig {

    /**
     * Default system clock using the JVM default time zone.
     *
     * @return the system {@link Clock}
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
