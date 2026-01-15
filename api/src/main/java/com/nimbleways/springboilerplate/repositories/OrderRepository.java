package com.nimbleways.springboilerplate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nimbleways.springboilerplate.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
