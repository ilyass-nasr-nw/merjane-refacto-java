package com.nimbleways.springboilerplate.repositories;

import org.springframework.data.repository.CrudRepository;

import com.nimbleways.springboilerplate.entities.Product;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {
	Optional<Product> findById(Long productId);

	Optional<Product> findFirstByName(String name);
}
