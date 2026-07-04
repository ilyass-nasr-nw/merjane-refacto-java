package com.nimbleways.merjane.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nimbleways.merjane.persistence.entities.Order;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findById(Long orderId);
}
