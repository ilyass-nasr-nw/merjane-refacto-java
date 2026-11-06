package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;

import java.time.LocalDate;

public class NormalProductStrategy implements ProductsStockStrategy {
    @Override
    public Resultat evaluer(Product product, LocalDate today) {
        return product.getAvailable() > 0 ? Resultat.DISPONIBLE : Resultat.DELAI;
    }
}
