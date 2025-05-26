package com.nimbleways.springboilerplate.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductTests {

    private Product normalProduct;
    private Product seasonalProduct;
    private Product expirableProduct;
    
    @BeforeEach
    void setUp() {
        
        normalProduct = new Product();
        normalProduct.setId(1L);
        normalProduct.setName("Produit Normal");
        normalProduct.setType("NORMAL");
        normalProduct.setAvailable(10);
        normalProduct.setLeadTime(5);
        
        seasonalProduct = new Product();
        seasonalProduct.setId(2L);
        seasonalProduct.setName("Produit Saisonnier");
        seasonalProduct.setType("SEASONAL");
        seasonalProduct.setAvailable(10);
        seasonalProduct.setLeadTime(5);
        seasonalProduct.setSeasonStartDate(LocalDate.now().minusDays(10));
        seasonalProduct.setSeasonEndDate(LocalDate.now().plusDays(20));
        
        expirableProduct = new Product();
        expirableProduct.setId(3L);
        expirableProduct.setName("Produit Périssable");
        expirableProduct.setType("EXPIRABLE");
        expirableProduct.setAvailable(10);
        expirableProduct.setLeadTime(5);
        expirableProduct.setExpiryDate(LocalDate.now().plusDays(30));
    }
    
    @Test
    void testIsAvailable_NormalProduct() {
        assertTrue(normalProduct.isAvailable());
        
        normalProduct.setAvailable(0);
        assertFalse(normalProduct.isAvailable());
    }
    
    @Test
    void testIsAvailable_SeasonalProduct() {
        assertTrue(seasonalProduct.isAvailable());
        
        seasonalProduct.setSeasonEndDate(LocalDate.now().minusDays(1));
        assertFalse(seasonalProduct.isAvailable());
        
        seasonalProduct.setSeasonEndDate(LocalDate.now().plusDays(20));
        seasonalProduct.setSeasonStartDate(LocalDate.now().plusDays(1));
        assertFalse(seasonalProduct.isAvailable());
    }
    
    @Test
    void testIsAvailable_ExpirableProduct() {
        assertTrue(expirableProduct.isAvailable());
        
        expirableProduct.setExpiryDate(LocalDate.now().minusDays(1));
        assertFalse(expirableProduct.isAvailable());
    }
    
    @Test
    void testIsExpired() {
        assertFalse(normalProduct.isExpired());
        assertFalse(seasonalProduct.isExpired());
        assertFalse(expirableProduct.isExpired());
        
        expirableProduct.setExpiryDate(LocalDate.now().minusDays(1));
        assertTrue(expirableProduct.isExpired());
    }
    
    @Test
    void testIsOutOfSeason() {
        assertFalse(normalProduct.isOutOfSeason());
        assertFalse(expirableProduct.isOutOfSeason());
        assertFalse(seasonalProduct.isOutOfSeason());
        
        seasonalProduct.setSeasonEndDate(LocalDate.now().minusDays(1));
        assertTrue(seasonalProduct.isOutOfSeason());
        
        seasonalProduct.setSeasonEndDate(LocalDate.now().plusDays(20));
        seasonalProduct.setSeasonStartDate(LocalDate.now().plusDays(1));
        assertTrue(seasonalProduct.isOutOfSeason());
    }
    
    @Test
    void testIsLeadTimeExceedingSeasonEnd() {
        assertFalse(normalProduct.isLeadTimeExceedingSeasonEnd());
        assertFalse(expirableProduct.isLeadTimeExceedingSeasonEnd());
        assertFalse(seasonalProduct.isLeadTimeExceedingSeasonEnd());
        
        seasonalProduct.setLeadTime(30); 
        assertTrue(seasonalProduct.isLeadTimeExceedingSeasonEnd());
    }
    
    @Test
    void testDecrementAvailable() {
        assertEquals(10, normalProduct.getAvailable());
        
        normalProduct.decrementAvailable();
        assertEquals(9, normalProduct.getAvailable());
        
        normalProduct.setAvailable(0);
        normalProduct.decrementAvailable();
        assertEquals(0, normalProduct.getAvailable()); 
    }
}
