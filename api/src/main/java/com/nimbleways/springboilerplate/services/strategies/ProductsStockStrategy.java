package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;

import java.time.LocalDate;

public interface ProductsStockStrategy {
    Resultat evaluer(Product product, LocalDate today);

    enum Resultat {
        DISPONIBLE, DELAI, NON_DISPONIBLE, EXPIRE
    }
}
