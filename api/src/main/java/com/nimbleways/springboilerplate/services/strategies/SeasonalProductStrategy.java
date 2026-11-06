package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.entities.Product;

import java.time.LocalDate;

public class SeasonalProductStrategy implements ProductsStockStrategy {
    @Override
    public Resultat evaluer(Product product, LocalDate today) {
        if (today.isBefore(product.getSeasonStartDate()) || today.isAfter(product.getSeasonEndDate())) {
            return Resultat.NON_DISPONIBLE;
        }
        if (product.getAvailable() > 0) {
            return Resultat.DISPONIBLE;
        }
        return today.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())
                ? Resultat.NON_DISPONIBLE : Resultat.DELAI;
    }
}
