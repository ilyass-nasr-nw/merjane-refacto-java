package com.nimbleways.springboilerplate.helpers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
    Order test helper to create order objects
 */
public class OrderHelpers {


    public static Order buildOrder(Set<Product> products){

        var order = Order.builder().build();

        if(Objects.nonNull(products)) {
            order = order.toBuilder().items(products).build();
        }
        return order;

    }
}
