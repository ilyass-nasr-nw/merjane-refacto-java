package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

// WARN: Should not be changed during the exercise
@Service
public class NotificationService {

    public void sendDelayNotification(int leadTime, String productName) {
    	System.out.println("Delay Notification: Product '" + productName + "' has a lead time of " + leadTime + " days.");
    }

    public void sendOutOfStockNotification(String productName) {
    }

    public void sendExpirationNotification(String productName, LocalDate expiryDate) {
    }
}