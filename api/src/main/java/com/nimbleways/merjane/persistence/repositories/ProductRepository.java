package com.nimbleways.merjane.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nimbleways.merjane.persistence.entities.Product;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findById(Long productId);

    Optional<Product> findFirstByName(String name);
}
