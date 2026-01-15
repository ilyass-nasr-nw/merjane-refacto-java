package com.nimbleways.springboilerplate.services.processors;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Processor responsible for applying inventory rules to NORMAL products.
 *
 * <p>Business rules:
 * <ul>
 *   <li>If the product has stock: decrement {@code available} by 1 and save.</li>
 *   <li>If out of stock and {@code leadTime > 0}: persist leadTime and send a delay notification.</li>
 *   <li>If out of stock and {@code leadTime <= 0}: do nothing.</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
public class NormalProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    /**
     * Creates a {@link NormalProductProcessor}.
     *
     * @param productRepository repository used to persist product updates
     * @param notificationService service used to notify customers
     */
    public NormalProductProcessor(ProductRepository productRepository,
                                  NotificationService notificationService) {
        this.productRepository = Objects.requireNonNull(productRepository, "productRepository must not be null");
        this.notificationService = Objects.requireNonNull(notificationService, "notificationService must not be null");
    }

    /**
     * Checks whether this processor supports the given product.
     *
     * @param product the product to check
     * @return true if the product type is NORMAL, false otherwise
     */
    @Override
    public boolean supports(Product product) {
        return product != null && product.getProductType() == ProductType.NORMAL;
    }

    /**
     * Applies NORMAL product rules.
     *
     * @param product the product to process
     * @throws IllegalArgumentException if product is null
     */
    @Override
    public void process(Product product) {
        if (product == null) {
            log.error("Cannot process normal product: product is null");
            throw new IllegalArgumentException("product must not be null");
        }

        Integer available = product.getAvailable();
        int leadTime = product.getLeadTime() != null ? product.getLeadTime() : 0;

        log.debug("Processing normal product. productId={} name={} available={} leadTime={}",
                product.getId(),
                product.getName(),
                available,
                leadTime
        );

        if (available != null && available > 0) {
            product.setAvailable(available - 1);
            productRepository.save(product);

            log.info("Normal product sold successfully. productId={} name={} newAvailable={}",
                    product.getId(),
                    product.getName(),
                    product.getAvailable()
            );
            return;
        }

        if (leadTime > 0) {
            product.setLeadTime(leadTime);
            productRepository.save(product);
            notificationService.sendDelayNotification(leadTime, product.getName());

            log.info("Normal product out of stock. Delay notification sent. productId={} name={} leadTime={}",
                    product.getId(),
                    product.getName(),
                    leadTime
            );
            return;
        }

        log.info("Normal product out of stock with no lead time. No notification sent. productId={} name={}",
                product.getId(),
                product.getName()
        );
    }
}
