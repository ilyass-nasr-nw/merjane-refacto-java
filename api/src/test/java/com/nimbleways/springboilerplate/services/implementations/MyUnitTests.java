package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    private NotificationService notificationService;
    @Mock
    private ProductRepository productRepository;
    private ProductService productService;

    private final LocalDate today = LocalDate.of(2026, 6, 15);

    @BeforeEach
    void initialiserService() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-15T12:00:00Z"), ZoneOffset.UTC);
        productService = new ProductService(productRepository, notificationService, clock);
    }

    @Test
    public void test() {
        Product product = new Product(null, 15, 0, "NORMAL", "RJ45 Cable", null, null, null);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.notifyDelay(product.getLeadTime(), product);

        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        verify(productRepository, Mockito.times(1)).save(product);
        verify(notificationService, Mockito.times(1)).sendDelayNotification(product.getLeadTime(), product.getName());
    }

    @Test
    void doitDecrementerStockProduitNormal() {
        Product product = new Product(
                null, 5, 3, "NORMAL", "Cable", null, null, null
        );

        productService.process(product);

        assertEquals(2, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void doitUtiliserHorlogeSystemeParDefaut() {
        ProductService service = new ProductService(productRepository, notificationService);
        Product product = new Product(null, 5, 3, "NORMAL", "Cable", null, null, null);

        service.process(product);

        assertEquals(2, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void doitAnnoncerDelaiQuandStockProduitNormalEpuise() {
        Product product = new Product(
                null, 5, 0, "NORMAL", "Cable", null, null, null
        );

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService, Mockito.times(1)).sendDelayNotification(product.getLeadTime(), product.getName());
    }


    @Test
    public void doitDecrementerStockProduitSeasonal() {
        Product product = new Product(null, 15, 10, "SEASONAL", "RJ45 Cable", null,
                today.minusDays(1), today.plusDays(1));

        productService.process(product);

        assertEquals(9, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void doitVendreDerniereUniteProduitNormal() {
        Product product = new Product(null, 5, 1, "NORMAL", "Cable", null, null, null);

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void doitAnnoncerDelaiNulProduitNormalSansStock() {
        Product product = new Product(null, 0, 0, "NORMAL", "Cable", null, null, null);

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendDelayNotification(0, product.getName());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitVendreProduitSeasonalAuDebutSaison() {
        Product product = new Product(null, 5, 1, "SEASONAL", "Fruit", null,
                today, today.plusDays(30));

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void doitVendreProduitSeasonalALaFinSaison() {
        Product product = new Product(null, 5, 1, "SEASONAL", "Fruit", null,
                today.minusDays(30), today);

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void doitNotifierIndisponibiliteAvantSaisonSansSupprimerStock() {
        Product product = new Product(null, 5, 3, "SEASONAL", "Fruit", null,
                today.plusDays(1), today.plusDays(30));

        productService.process(product);

        assertEquals(3, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendOutOfStockNotification(product.getName());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitNotifierIndisponibiliteApresSaisonSansSupprimerStock() {
        Product product = new Product(null, 5, 3, "SEASONAL", "Fruit", null,
                today.minusDays(30), today.minusDays(1));

        productService.process(product);

        assertEquals(3, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendOutOfStockNotification(product.getName());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitAnnoncerDelaiProduitSeasonalSansStock() {
        Product product = new Product(null, 5, 0, "SEASONAL", "Fruit", null,
                today.minusDays(1), today.plusDays(30));

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendDelayNotification(5, product.getName());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitAnnoncerDelaiLivraisonDernierJourSaison() {
        Product product = new Product(null, 5, 0, "SEASONAL", "Fruit", null,
                today.minusDays(1), today.plusDays(5));

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendDelayNotification(5, product.getName());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitNotifierIndisponibiliteLivraisonApresSaison() {
        Product product = new Product(null, 6, 0, "SEASONAL", "Fruit", null,
                today.minusDays(1), today.plusDays(5));

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendOutOfStockNotification(product.getName());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitDecrementerStockProduitExpirable() {
        Product product = new Product(null, 5, 3, "EXPIRABLE", "Lait",
                today.plusDays(1), null, null);

        productService.process(product);

        assertEquals(2, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void doitNotifierExpirationProduitExpireHier() {
        Product product = new Product(null, 5, 3, "EXPIRABLE", "Lait",
                today.minusDays(1), null, null);

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendExpirationNotification(product.getName(), product.getExpiryDate());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitNotifierExpirationProduitExpirantAujourdhui() {
        Product product = new Product(null, 5, 3, "EXPIRABLE", "Lait", today, null, null);

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendExpirationNotification(product.getName(), product.getExpiryDate());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitNotifierExpirationProduitExpireSansStock() {
        Product product = new Product(null, 5, 0, "EXPIRABLE", "Lait",
                today.minusDays(1), null, null);

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendExpirationNotification(product.getName(), product.getExpiryDate());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitAnnoncerDelaiProduitExpirableSansStockAvantExpiration() {
        Product product = new Product(null, 5, 0, "EXPIRABLE", "Lait",
                today.plusDays(6), null, null);

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendDelayNotification(5, product.getName());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitNotifierIndisponibiliteLivraisonJourExpiration() {
        Product product = new Product(null, 5, 0, "EXPIRABLE", "Lait",
                today.plusDays(5), null, null);

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendOutOfStockNotification(product.getName());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitNotifierIndisponibiliteLivraisonApresExpiration() {
        Product product = new Product(null, 6, 0, "EXPIRABLE", "Lait",
                today.plusDays(5), null, null);

        productService.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendOutOfStockNotification(product.getName());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void doitRefuserTypeProduitInconnu() {
        Product product = new Product(null, 5, 3, "EXPIRED", "Lait",
                today.plusDays(1), null, null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productService.process(product));

        assertEquals("Unsupported product type: EXPIRED", exception.getMessage());
        assertEquals(3, product.getAvailable());
        verifyNoInteractions(productRepository, notificationService);
    }

    @Test
    void doitRefuserTypeProduitAbsent() {
        Product product = new Product(null, 5, 3, null, "Cable", null, null, null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productService.process(product));

        assertEquals("Unsupported product type: null", exception.getMessage());
        assertEquals(3, product.getAvailable());
        verifyNoInteractions(productRepository, notificationService);
    }
}