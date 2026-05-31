package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class MyController {
	@Autowired
	private ProductService ps;

	@Autowired
	private ProductRepository pr;

	@Autowired
	private OrderRepository or;

	@PostMapping("{orderId}/processOrder")
	@ResponseStatus(HttpStatus.OK)
	public ProcessOrderResponse processOrder(@PathVariable Long orderId) {
		Order order = or.findById(orderId).get();
		System.out.println(order);
		List<Long> ids = new ArrayList<>();
		ids.add(orderId);
		Set<Product> products = order.getItems();
		for (Product p : products) {
			if (p.getType().equals("NORMAL")) {
				int leadTime = p.getLeadTime();
				if (p.getAvailable() > 0) {
					if (leadTime == 0) {
						ps.notifyDelay(leadTime, p);
					}
					p.setAvailable(p.getAvailable() - 1);
					pr.save(p);
				} else {
					if (leadTime > 0) {
						ps.notifyDelay(leadTime, p);
					}
				}
			} else if (p.getType().equals("SEASONAL")) {
				// Add new season rules
				if ((LocalDate.now().isAfter(p.getSeasonStartDate()) && LocalDate.now().isBefore(p.getSeasonEndDate())
						&& p.getAvailable() > 0)) {
					p.setAvailable(p.getAvailable() - 1);
					pr.save(p);
				} else {
					ps.handleSeasonalProduct(p);
				}
			} else if (p.getType().equals("EXPIRABLE")) {
				if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
					p.setAvailable(p.getAvailable() - 1);
					pr.save(p);
				} else {
					ps.handleExpiredProduct(p);
				}
			}
		}

		return new ProcessOrderResponse(order.getId());
	}
	
	@PostMapping(path = "/createOrder",  consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public ProcessOrderResponse createOrder(@RequestBody Order order) {
		Order saved = or.save(order);
		return new ProcessOrderResponse(saved.getId());
	}
	
	@PostMapping(path = "/addProduct", consumes = "application/json", produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Product addProduct(@RequestBody Product product) {
	    Product saved = pr.save(product);
	    return saved;
	}
	
	@GetMapping(path = "/listProduct", produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	public List<Product> listProduct() {
	    return pr.findAll();
	}
	
	@GetMapping(path = "/listOrders", produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	public List<Order> listOrder() {
	    return or.findAll();
	}
}
