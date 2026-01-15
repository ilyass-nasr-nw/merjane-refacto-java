package com.nimbleways.springboilerplate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
