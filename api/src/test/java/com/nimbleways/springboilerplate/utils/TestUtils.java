package com.nimbleways.springboilerplate.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtils {
    public TestUtils() {
    }

    public static String readJsonFile(String fileName, Class<?> testClass) {
        try (InputStream resourceAsStream = testClass.getResourceAsStream(fileName)) {
            assert resourceAsStream != null : "file '" + fileName + "' not found in resources";
            return new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalArgumentException("IOException thrown while reading test file: ", exception);
        }
    }
}
