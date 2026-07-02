package com.nimbleways.springboilerplate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nimbleways.springboilerplate.entities.Order;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findById(Long orderId);
}
