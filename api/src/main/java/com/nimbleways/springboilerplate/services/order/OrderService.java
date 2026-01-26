import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.OrderException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Service
public class OrderService {

    private final ProductService productService;
    private final OrderRepository orderRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public ProcessOrderResponse processOrder(Long orderId) throws OrderException {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            throw new OrderException("Order not found ");
        }

        Order order = optionalOrder.get();
        Set<Product> products = order.getItems();

        if (products == null || products.isEmpty()) {
            return new ProcessOrderResponse(order.getId());
        }
        products.stream().forEach(product -> {
            try {
                productService.processProduct(product);
            } catch (Exception e) {
           logger.error("Error for product {}", product, e);
            }
        });

        return new ProcessOrderResponse(order.getId());
    }

}