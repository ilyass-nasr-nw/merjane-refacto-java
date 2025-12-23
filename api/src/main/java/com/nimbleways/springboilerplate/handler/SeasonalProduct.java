package com.nimbleways.springboilerplate.handler;

import com.nimbleways.springboilerplate.constants.ProductType;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SeasonalProduct implements ProductHandler {
	private final ProductRepository productRepository;
	private final NotificationService notificationService;

	@Override
	public boolean supports(ProductType type) {
		return type == ProductType.SEASONAL;
	}

	@Override
	public void manage(Product product) {
		LocalDate today = LocalDate.now();
		int available = product.getAvailable();
		int leadTime = product.getLeadTime();

		boolean inSeasonToday = today.isAfter(product.getSeasonStartDate())
				&& today.isBefore(product.getSeasonEndDate());
		boolean inSeasonAfterLeadTime = (today.plusDays(leadTime).isAfter(product.getSeasonStartDate())
				&& today.plusDays(leadTime).isBefore(product.getSeasonEndDate()));

		if (inSeasonToday && available > 0) {
			product.setAvailable(available - 1);
		} else if (!inSeasonAfterLeadTime) {
			notificationService.sendOutOfStockNotification(product.getName());
			product.setAvailable(0);
		} else {
			notificationService.sendDelayNotification(leadTime, product.getName());
		}

		productRepository.save(product);
	}	
}
