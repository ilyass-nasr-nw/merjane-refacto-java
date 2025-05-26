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
    
   
    public void handleSeasonalProduct(Product p) {
        if (p.isLeadTimeExceedingSeasonEnd()) {
            // Si le délai dépasse la fin de saison, notifier l'indisponibilité
            handleOutOfStockProduct(p);
        } else if (LocalDate.now().isBefore(p.getSeasonStartDate())) {
            // Si nous sommes avant le début de saison, notifier l'indisponibilité
            handleOutOfStockProduct(p);
        } else {
            // Sinon, notifier le délai normal
            notifyDelay(p.getLeadTime(), p);
        }
    }
    
    
    public void handleExpiredProduct(Product p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            // Si le produit est disponible et non expiré, décrémente la disponibilité
            p.setAvailable(p.getAvailable() - 1);
            pr.save(p);
        } else {
            // Si le produit est expiré, notifier l'expiration
            ns.sendExpirationNotification(p.getName(), p.getExpiryDate());
            p.setAvailable(0);
            pr.save(p);
        }
    }
    
    
    private void handleOutOfStockProduct(Product p) {
        ns.sendOutOfStockNotification(p.getName());
        p.setAvailable(0);
        pr.save(p);
    }
    
    
    public void processOrder(Product product) {
        // Si le produit n'est pas disponible, ne rien faire
        if (product.getAvailable() <= 0) {
            return;
        }
        
        // Décrémente la disponibilité du produit
        product.decrementAvailable();
        pr.save(product);
        
        // Si le produit est maintenant en rupture de stock, gérer selon son type
        if (product.getAvailable() <= 0) {
            handleProductStockout(product);
        }
    }
    
    
    private void handleProductStockout(Product product) {
        switch (product.getType()) {
            case "SEASONAL":
                handleSeasonalProduct(product);
                break;
            case "EXPIRABLE":
                handleExpiredProduct(product);
                break;
            default: // NORMAL
                notifyDelay(product.getLeadTime(), product);
                break;
        }
    }
}
