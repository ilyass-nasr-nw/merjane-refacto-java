package com.nimbleways.springboilerplate.services.implementations.strategy;

import com.nimbleways.springboilerplate.entities.product.SeasonalProduct;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;


@ExtendWith(SpringExtension.class)
@UnitTest
class ProcessSeasonalProductStrategyTest {
    @Mock
    ProductRepository productRepository;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    ProcessSeasonalProductStrategy strategy;

    @Test
    void should_process_product_in_season_with_available_stock() {
        SeasonalProduct product = Mockito.mock(SeasonalProduct.class);

        Mockito.when(product.getName()).thenReturn("Watermelon");
        Mockito.when(product.getAvailable()).thenReturn(5);
        Mockito.when(product.getSeasonStartDate()).thenReturn(LocalDate.now().minusDays(5));
        Mockito.when(product.getSeasonEndDate()).thenReturn(LocalDate.now().plusDays(5));

        strategy.processProduct(product);

        Mockito.verify(productRepository).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    @Test
    void should_notify_and_set_stock_to_zero_when_lead_time_misses_season() {
        SeasonalProduct product = Mockito.mock(SeasonalProduct.class);

        Mockito.when(product.getName()).thenReturn("Dates");
        Mockito.when(product.getAvailable()).thenReturn(10);
        Mockito.when(product.getSeasonStartDate()).thenReturn(LocalDate.now().minusMonths(2));
        Mockito.when(product.getSeasonEndDate()).thenReturn(LocalDate.now().minusDays(1));
        Mockito.when(product.getLeadTime()).thenReturn(10);

        strategy.processProduct(product);

        Mockito.verify(notificationService).sendOutOfStockNotification("Dates");
        Mockito.verify(product).setAvailable(0);
        Mockito.verify(productRepository).save(product);
    }
}
