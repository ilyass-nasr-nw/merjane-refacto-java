package com.nimbleways.springboilerplate.repositories;

import com.nimbleways.springboilerplate.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
