package com.nimbleways.springboilerplate.services.implementations;


import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.enums.OrderType;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Set;

@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final OrderRepository orderRepository;

    public OrderService(ProductRepository productRepository, ProductService productService, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.orderRepository = orderRepository;
    }

    /**
     * Processes an order by its identifier.
     *
     * <p>This method retrieves the order from the database and iterates over
     * its associated products. Each product is processed according to it type
     *
     * @param id the unique identifier of the order to process
     * @return a {@code ProcessOrderResponse} containing the processed order id
     * @throws NotFoundException if no order is found for the given id
     */
    @Transactional
    public ProcessOrderResponse processOrder(Long id){
        // find order by id, if it doesn't exist throw exception
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("order not found"));

        Set<Product> products = order.getItems();
        // loop over order product
        for(Product product : products){
            if(product.getType() == OrderType.NORMAL){
                handleNormalProduct(product);
            } else if (product.getType() == OrderType.SEASONAL) {
                handleSeasonalProduct(product);
            } else if (product.getType() == OrderType.EXPIRABLE) {
                handleExpirableProduct(product);
            }
        }
        return new ProcessOrderResponse(id);
    }

    private void handleNormalProduct(Product product){
        // If product is available, decrement available product
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            // if not available verify lead time
            int leadTime = product.getLeadTime();
            // if exist notify client
            if (leadTime > 0) {
                productService.notifyDelay(leadTime, product);
            }
        }
    }

    private void handleSeasonalProduct(Product product){
        // check if it is in the  season
        if ((LocalDate.now().isAfter(product.getSeasonStartDate()) && LocalDate.now().isBefore(product.getSeasonEndDate())
                && product.getAvailable() > 0)) {
            // decrement availability
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            // handle outside season
            productService.handleSeasonalProduct(product);
        }
    }


    private void handleExpirableProduct(Product product){
        // Check if product is available AND not expired
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            // handle expired
            productService.handleExpiredProduct(product);
        }
    }
}
