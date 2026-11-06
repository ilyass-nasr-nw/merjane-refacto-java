package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;

import java.time.LocalDate;

public class ExpirableProductStrategy implements ProductsStockStrategy {
    @Override
    public Resultat evaluer(Product product, LocalDate today) {
        if (!today.isBefore(product.getExpiryDate())) {
            return Resultat.EXPIRE;
        }
        if (product.getAvailable() > 0) {
            return Resultat.DISPONIBLE;
        }
        return today.plusDays(product.getLeadTime()).isBefore(product.getExpiryDate())
                ? Resultat.DELAI : Resultat.NON_DISPONIBLE;
    }
}
