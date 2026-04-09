package com.nimbleways.springboilerplate.services.implementations;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

// WARN: Should not be changed during the exercise
@Service
public class NotificationServiceImpl {

    public void sendDelayNotification(int leadTime, String productName) {
    }

    public void sendOutOfStockNotification(String productName) {
    }

    public void sendExpirationNotification(String productName, LocalDate expiryDate) {
    }
}