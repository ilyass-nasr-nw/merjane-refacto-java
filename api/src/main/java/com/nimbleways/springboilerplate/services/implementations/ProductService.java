package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import com.nimbleways.springboilerplate.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void processProduct(Product p) {
        switch (p.getType()) {
            case NORMAL:
                handleNormalProduct((NormalProduct) p);
                break;
            case SEASONAL:
                handleSeasonalProduct((SeasonalProduct) p);
                break;
            case EXPIRABLE:
                handleExpirableProduct((ExpirableProduct) p);
                break;
            case FLASHSALE:
                handleFlashSaleProduct((FlashSaleProduct) p);
                break;
        }
    }

    private void handleNormalProduct(NormalProduct np) {
        np.process();
        if (np.getLeadTime() > 0) {
            notifyDelay(np.getLeadTime(), np);
        }
        pr.save(np);
    }

    private void handleSeasonalProduct(SeasonalProduct sp) {
        sp.process();
        LocalDate now = LocalDate.now();
        if (now.plusDays(sp.getLeadTime()).isAfter(sp.getSeasonEndDate())
                || sp.getSeasonStartDate().isAfter(LocalDate.now())) {
            ns.sendOutOfStockNotification(sp.getName());
        } else {
            notifyDelay(sp.getLeadTime(), sp);
        }
        pr.save(sp);
    }

    private void handleExpirableProduct(ExpirableProduct ep) {
        ep.process();
        if (ep.getExpiryDate().isAfter(LocalDate.now())){
            ns.sendExpirationNotification(ep.getName(), ep.getExpiryDate());
        } else {
            ns.sendOutOfStockNotification(ep.getName());
        }
        pr.save(ep);
    }

    private void handleFlashSaleProduct(FlashSaleProduct fsp) {
        fsp.process();
        LocalDate now = LocalDate.now();
        if (now.isAfter(fsp.getFlashSaleEndDate())) {
            ns.sendExpirationNotification(fsp.getName(), fsp.getFlashSaleEndDate());
        } else if (fsp.getMaxQuantity() == 0) {
            ns.sendOutOfStockNotification(fsp.getName());
        }
        pr.save(fsp);
    }
}