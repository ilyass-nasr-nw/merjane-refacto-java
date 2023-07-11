package com.nimbleways.springboilerplate.utils.junitextension;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class SetupTestDatabaseExtension extends AbstractBeforeAllTestsExtension {
    private static final PostgreSQLContainer<?> testDatabase = new PostgreSQLContainer<>("postgres:14.0");

    @Override
    void setup() {
        testDatabase.start();
    }

    @Override
    public void close() {
        testDatabase.stop();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + testDatabase.getJdbcUrl(),
                    "spring.datasource.username=" + testDatabase.getUsername(),
                    "spring.datasource.password=" + testDatabase.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
