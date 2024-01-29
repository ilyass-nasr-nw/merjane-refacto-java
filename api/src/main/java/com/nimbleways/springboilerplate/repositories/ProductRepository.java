package com.nimbleways.springboilerplate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nimbleways.springboilerplate.entities.Product;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

}
