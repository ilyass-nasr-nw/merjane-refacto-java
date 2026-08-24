package com.nimbleways.springboilerplate.services;

import java.time.LocalDate;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationServiceTest {

    private final NotificationService notificationService = new NotificationService();

    @Test
    void exposesTheNotificationServiceContract() throws NoSuchMethodException {
        assertPublicVoidMethod("sendDelayNotification", int.class, String.class);
        assertPublicVoidMethod("sendOutOfStockNotification", String.class);
        assertPublicVoidMethod("sendExpirationNotification", String.class, LocalDate.class);
    }

    @Test
    void notificationMethodsDoNotThrow() {
        assertDoesNotThrow(() -> notificationService.sendDelayNotification(2, "Product name"));
        assertDoesNotThrow(() -> notificationService.sendOutOfStockNotification("Product name"));
        assertDoesNotThrow(() -> notificationService.sendExpirationNotification("Product name", LocalDate.now()));
    }

    private void assertPublicVoidMethod(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        var method = NotificationService.class.getDeclaredMethod(methodName, parameterTypes);
        assertTrue(Modifier.isPublic(method.getModifiers()));
        assertEquals(void.class, method.getReturnType());
    }
}
