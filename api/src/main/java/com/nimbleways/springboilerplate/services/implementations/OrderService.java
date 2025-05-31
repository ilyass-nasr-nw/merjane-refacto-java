package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.NotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.nimbleways.springboilerplate.enumerations.ProductTypeEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final ProductService productService;
    private final OrderRepository orderRepository;


    public ProcessOrderResponse processOrder(Long orderId) throws NotFoundException {
        log.info("Start service processOrder for order with id : {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

        Set<Product> products = order.getItems();
        List<Product> productsToSave = new ArrayList<>();

        products.forEach(product -> {
            switch (product.getType()) {
                case NORMAL:
                    productService.processNormalProduct(product, productsToSave);
                    break;
                case SEASONAL:
                    productService.processSeasonalProduct(product, productsToSave);
                    break;
                case EXPIRABLE:
                    productService.processExpirableProduct(product, productsToSave);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid product type");
            }
        });
        productService.massiveSave(productsToSave);
        log.info("End service processOrder for order with id : {}", orderId);
        return new ProcessOrderResponse(order.getId());

    }
}
