package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import com.nimbleways.springboilerplate.businessservice.IOrderBusinessService;
import com.nimbleways.springboilerplate.businessservice.IProductBusinessService;
import com.nimbleways.springboilerplate.dto.ProcessOrder;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.exceptions.OrderException;
import com.nimbleways.springboilerplate.services.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService  implements IProductService {

   private final IProductBusinessService productBusinessService;

   private  final IOrderBusinessService orderBusinessService;

    private final NotificationService notificationService;


    private void notifyDelay(int leadTime, Product product) {
        product.setLeadTime(leadTime);
        productBusinessService.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }


    private void handleSeasonalProduct(Product product) {
        if ((LocalDate.now().isAfter(product.getSeasonStartDate()) && LocalDate.now().isBefore(product.getSeasonEndDate())
                && product.getAvailable() > 0)) {
            product.setAvailable(product.getAvailable() - 1);
            productBusinessService.save(product);
        }else {
            if (LocalDate.now().plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
                product.setAvailable(0);
                sendOutOfStockNotification(product);
            } else if (product.getSeasonStartDate().isAfter(LocalDate.now())) {
                sendOutOfStockNotification(product);
            } else {
                notifyDelay(product.getLeadTime(), product);
            }
        }
    }

    private void sendOutOfStockNotification(Product product){
        notificationService.sendOutOfStockNotification(product.getName());
        productBusinessService.save(product);
    }

    private void handleExpiredProduct(Product product) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
            productBusinessService.save(product);
        } else {
            if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
                product.setAvailable(product.getAvailable() - 1);
                productBusinessService.save(product);
            } else {
                notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
                product.setAvailable(0);
                productBusinessService.save(product);
            }
        }
    }

    @Override
    public ProcessOrder processOrder(ProcessOrder processOrder) {
        var orderId=processOrder.id();
        Order order = orderBusinessService.findById(orderId);
        if(Objects.isNull(order)) throw new OrderException();
        log.info("Order processing ... => "+ order);
        Set<Product> products = order.getItems();
        for (Product p : products) {
            switch (p.getType()) {
                case NORMAL:
                    handleNormalProduct(p);
                    break;
                case SEASONAL:
                    handleSeasonalProduct(p);
                    break;
                case EXPIRABLE:
                    handleExpiredProduct(p);
                    break;
                default:
                case FLASHSALE:
                    handleFlashableProduct(p);
                    break;
            }
        }
        return new ProcessOrder(order.getId());
    }

    private void handleFlashableProduct(Product product){
        if(product.getSeasonStartDate().isBefore(LocalDate.now())
        && product.getSeasonEndDate().isAfter(LocalDate.now())
        && product.getAvailable()>0 ){
            product.setAvailable(product.getAvailable() - 1);
            productBusinessService.save(product);
        }
    }
    private void handleNormalProduct(Product product){
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productBusinessService.save(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                notifyDelay(leadTime, product);
            }
        }
    }

}