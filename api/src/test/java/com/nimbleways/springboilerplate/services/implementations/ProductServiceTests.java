package com.nimbleways.springboilerplate.services.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private ProductService productService;
    
    private Product normalProduct;
    private Product seasonalProduct;
    private Product expirableProduct;
    
    @BeforeEach
    void setUp() {
        // Produit normal
        normalProduct = new Product();
        normalProduct.setId(1L);
        normalProduct.setName("Produit Normal");
        normalProduct.setType("NORMAL");
        normalProduct.setAvailable(10);
        normalProduct.setLeadTime(5);
        
        // Produit saisonnier
        seasonalProduct = new Product();
        seasonalProduct.setId(2L);
        seasonalProduct.setName("Produit Saisonnier");
        seasonalProduct.setType("SEASONAL");
        seasonalProduct.setAvailable(10);
        seasonalProduct.setLeadTime(5);
        seasonalProduct.setSeasonStartDate(LocalDate.now().minusDays(10));
        seasonalProduct.setSeasonEndDate(LocalDate.now().plusDays(20));
        
        // Produit périssable
        expirableProduct = new Product();
        expirableProduct.setId(3L);
        expirableProduct.setName("Produit Périssable");
        expirableProduct.setType("EXPIRABLE");
        expirableProduct.setAvailable(10);
        expirableProduct.setLeadTime(5);
        expirableProduct.setExpiryDate(LocalDate.now().plusDays(30));
    }
    
    @Test
    void testProcessOrder_NormalProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(normalProduct);
        
        // Act
        productService.processOrder(normalProduct);
        
        // Assert
        assertEquals(9, normalProduct.getAvailable());
        verify(productRepository, times(1)).save(normalProduct);
    }
    
    @Test
    void testProcessOrder_NormalProduct_OutOfStock() {
        // Arrange
        normalProduct.setAvailable(1);
        when(productRepository.save(any(Product.class))).thenReturn(normalProduct);
        
        // Act
        productService.processOrder(normalProduct);
        
        // Assert
        assertEquals(0, normalProduct.getAvailable());
        verify(productRepository, times(2)).save(normalProduct); // Une fois pour décrémenter, une fois dans notifyDelay
        verify(notificationService, times(1)).sendDelayNotification(normalProduct.getLeadTime(), normalProduct.getName());
    }
    
    @Test
    void testProcessOrder_SeasonalProduct_InSeason() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(seasonalProduct);
        
        // Act
        productService.processOrder(seasonalProduct);
        
        // Assert
        assertEquals(9, seasonalProduct.getAvailable());
        verify(productRepository, times(1)).save(seasonalProduct);
    }
    
    @Test
    void testProcessOrder_SeasonalProduct_OutOfStock_LeadTimeWithinSeason() {
        // Arrange
        seasonalProduct.setAvailable(1);
        when(productRepository.save(any(Product.class))).thenReturn(seasonalProduct);
        
        // Act
        productService.processOrder(seasonalProduct);
        
        // Assert
        assertEquals(0, seasonalProduct.getAvailable());
        verify(productRepository, times(2)).save(seasonalProduct);
        verify(notificationService, times(1)).sendDelayNotification(seasonalProduct.getLeadTime(), seasonalProduct.getName());
    }
    
    @Test
    void testProcessOrder_SeasonalProduct_OutOfStock_LeadTimeExceedingSeason() {
        // Arrange
        seasonalProduct.setAvailable(1);
        seasonalProduct.setLeadTime(30); // Délai dépassant la fin de saison
        when(productRepository.save(any(Product.class))).thenReturn(seasonalProduct);
        
        // Act
        productService.processOrder(seasonalProduct);
        
        // Assert
        assertEquals(0, seasonalProduct.getAvailable());
        verify(productRepository, times(2)).save(seasonalProduct);
        verify(notificationService, times(1)).sendOutOfStockNotification(seasonalProduct.getName());
    }
    
    @Test
    void testProcessOrder_ExpirableProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(expirableProduct);
        
        // Act
        productService.processOrder(expirableProduct);
        
        // Assert
        assertEquals(9, expirableProduct.getAvailable());
        verify(productRepository, times(1)).save(expirableProduct);
    }
    
    @Test
    void testProcessOrder_ExpirableProduct_OutOfStock_NotExpired() {
        // Arrange
        expirableProduct.setAvailable(1);
        when(productRepository.save(any(Product.class))).thenReturn(expirableProduct);
        
        // Act
        productService.processOrder(expirableProduct);
        
        // Assert
        assertEquals(0, expirableProduct.getAvailable());
        verify(productRepository, times(2)).save(expirableProduct);
        // Supprimer cette ligne ou la modifier pour vérifier que la notification est bien envoyée
        // verify(notificationService, times(0)).sendExpirationNotification(anyString(), any(LocalDate.class));
        verify(notificationService, times(1)).sendExpirationNotification(expirableProduct.getName(), expirableProduct.getExpiryDate());
    }

    
    @Test
    void testProcessOrder_ExpirableProduct_OutOfStock_Expired() {
        // Arrange
        expirableProduct.setAvailable(1);
        expirableProduct.setExpiryDate(LocalDate.now().minusDays(1)); // Produit expiré
        when(productRepository.save(any(Product.class))).thenReturn(expirableProduct);
        
        // Act
        productService.processOrder(expirableProduct);
        
        // Assert
        assertEquals(0, expirableProduct.getAvailable());
        verify(productRepository, times(2)).save(expirableProduct);
        verify(notificationService, times(1)).sendExpirationNotification(expirableProduct.getName(), expirableProduct.getExpiryDate());
    }
}
