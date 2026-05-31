package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    ProductRepository pr;

    @Autowired
    NotificationService ns;

    public void notifyDelay(int leadTime, Product p) {
        p.setLeadTime(leadTime);
        pr.save(p);
        ns.sendDelayNotification(leadTime, p.getName());
    }
    
    // -------------------------
    // NORMAL PRODUCT (IMPORTANT FIX)
    // -------------------------
    
    
    private void handleLeadTimeRule(Product p) {
        if (p.getLeadTime() != null && p.getLeadTime() == 0) {
            ns.sendDelayNotification(0, p.getName());
        }
    }
    
    public void handleNormalProduct(Product p) {

    	 handleLeadTimeRule(p); // 🔥 ALWAYS APPLIES FIRST

    	    if (p.getAvailable() > 0) {
    	        p.setAvailable(p.getAvailable() - 1);
    	        pr.save(p);
    	    } else {
    	        ns.sendDelayNotification(p.getLeadTime(), p.getName());
    	    }
    }

    public void handleSeasonalProduct(Product p) {
    	  handleLeadTimeRule(p); // 🔥 GLOBAL RULE

    	    if (LocalDate.now().plusDays(p.getLeadTime()).isAfter(p.getSeasonEndDate())) {
    	        ns.sendOutOfStockNotification(p.getName());
    	        p.setAvailable(0);
    	        pr.save(p);

    	    } else if (p.getSeasonStartDate().isAfter(LocalDate.now())) {
    	        ns.sendOutOfStockNotification(p.getName());
    	        pr.save(p);

    	    } else {
    	        notifyDelay(p.getLeadTime(), p);
    	    }
    }

    public void handleExpiredProduct(Product p) {
    	handleLeadTimeRule(p); // 🔥 GLOBAL RULE

        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - 1);
        } else {
            ns.sendExpirationNotification(p.getName(), p.getExpiryDate());
            p.setAvailable(0);
        }

        pr.save(p);
    }
}