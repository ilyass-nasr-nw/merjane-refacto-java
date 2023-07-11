package com.nimbleways.springboilerplate.utils.Annotations;


import org.junit.jupiter.api.Timeout;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
public @interface UnitTest {
}
