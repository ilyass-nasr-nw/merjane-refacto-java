package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductService;

@Service
public class ProductServiceDefault implements ProductService {

    private final ProductRepository productRepository;

    private final NotificationService notificationService;

    
    public ProductServiceDefault(final ProductRepository productRepository, final NotificationService notificationService) {
		this.productRepository = productRepository;
		this.notificationService = notificationService;
	}

    @Override
	public void notifyDelay(final int leadTime, final Product product) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }

    @Override
    public void handleSeasonalProduct(final Product product) {
        if (LocalDate.now().plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
        } else if (product.getSeasonStartDate().isAfter(LocalDate.now())) {
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
        } else {
            notifyDelay(product.getLeadTime(), product);
        }
    }

    @Override
    public void handleExpiredProduct(final Product product) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);
        }
    }

	@Override
	public void handleFlashableProduct(final Product product) {
		LocalDate now = LocalDate.now();

		if (now.isAfter(product.getFlashStartDate()) && now.isBefore(product.getFlashEndDate())) {
		    if (product.getAvailable() > 0 && product.getMaxQuantity() > 0) {
		    	product.setAvailable(product.getAvailable() - 1);
		    	product.setMaxQuantity(product.getMaxQuantity() - 1);
		    } else {
		        notificationService.sendOutOfStockNotification(product.getName());
		        product.setAvailable(0);
		    }
		} else if (now.isAfter(product.getFlashEndDate())) {
		    notificationService.sendExpirationNotification(product.getName(), product.getFlashEndDate());
		    product.setAvailable(0);
		}

		productRepository.save(product);
	}
    
    
}