package com.nimbleways.springboilerplate.repositories;

import org.springframework.data.repository.CrudRepository;
import com.nimbleways.springboilerplate.entities.Order;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {
    Optional<Order> findById(Long orderId);
}
