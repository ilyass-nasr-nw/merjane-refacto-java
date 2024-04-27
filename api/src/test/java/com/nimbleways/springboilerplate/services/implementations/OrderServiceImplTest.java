package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(MockitoJUnitRunner.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;



    @Mock
    private ProductService productService;
    @InjectMocks
    private NormalProductStrategy normalProductStrategy;
    @Mock
    private SeasonalProductStrategy seasonalProductStrategy = new SeasonalProductStrategy();
    @Mock
    private ExpirableProductStrategy expirableProductStrategy = new ExpirableProductStrategy();
    @Mock
    private FlashSaleProductStrategy flashSaleProductStrategy = new FlashSaleProductStrategy();

    @Spy
    private ProductProcessor productProcessor = new ProductProcessorImpl(normalProductStrategy, seasonalProductStrategy, expirableProductStrategy, flashSaleProductStrategy);



    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private Product normalProduct;
    private Product seasonalProduct;
    private Product expirableProduct;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        order = new Order();
        order.setId(1L);

        normalProduct = new Product();
        normalProduct.setId(1L);
        normalProduct.setType("NORMAL");
        normalProduct.setAvailable(1);

        seasonalProduct = new Product();
        seasonalProduct.setId(2L);
        seasonalProduct.setType("SEASONAL");
        seasonalProduct.setAvailable(1);
        seasonalProduct.setSeasonStartDate(LocalDate.now().minusDays(1));
        seasonalProduct.setSeasonEndDate(LocalDate.now().plusDays(1));

        expirableProduct = new Product();
        expirableProduct.setId(3L);
        expirableProduct.setType("EXPIRABLE");
        expirableProduct.setAvailable(1);
        expirableProduct.setExpiryDate(LocalDate.now().plusDays(1));
    }

    // Normal product is available
    @Test
    public void testProcessOrder_NormalProductAvailable() {
        Set<Product> items = new HashSet<>();
        items.add(normalProduct);
        order.setItems(items);
        Mockito.when(productService.save(Mockito.any())).thenReturn(normalProduct);
        orderService.processOrderNormal(normalProduct);

        assertEquals(0, normalProduct.getAvailable());
    }

    // SEASONAL product is available
    @Test
    public void testProcessOrder_SeasonalProductAvailable() {
        Set<Product> items = new HashSet<>();
        items.add(seasonalProduct);
        order.setItems(items);

        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Long orderId = orderService.processOrder(1L);

        assertEquals(1L, orderId);
        assertEquals(0, seasonalProduct.getAvailable());
    }

    // SEASONAL product is not available
    @Test
    public void testProcessOrder_SeasonalProductNotAvailable() {
        seasonalProduct.setAvailable(0);
        Set<Product> items = new HashSet<>();
        items.add(seasonalProduct);
        order.setItems(items);

        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Long orderId = orderService.processOrder(1L);

        assertEquals(1L, orderId);
        Mockito.verify(productService, Mockito.times(1)).handleSeasonalProduct(seasonalProduct);
    }

    // EXPIRABLE product is available
    @Test
    public void testProcessOrder_ExpirableProductAvailable() {
        Set<Product> items = new HashSet<>();
        items.add(expirableProduct);
        order.setItems(items);

        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Long orderId = orderService.processOrder(1L);

        assertEquals(1L, orderId);
        assertEquals(0, expirableProduct.getAvailable());
    }

    // EXPIRABLE product is not available
    @Test
    public void testProcessOrder_ExpirableProductNotAvailable() {
        expirableProduct.setAvailable(0);
        Set<Product> items = new HashSet<>();
        items.add(expirableProduct);
        order.setItems(items);

        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Long orderId = orderService.processOrder(1L);

        assertEquals(1L, orderId);
        Mockito.verify(productService, Mockito.times(1)).handleExpiredProduct(expirableProduct);
    }

    // FLASHSALE product is available
    @Test
    public void testProcessOrder_FlashSaleProductAvailable() {
        Product flashSaleProduct = new Product();
        flashSaleProduct.setId(4L);
        flashSaleProduct.setType("FLASHSALE");
        flashSaleProduct.setAvailable(1);

        Set<Product> items = new HashSet<>();
        items.add(flashSaleProduct);
        order.setItems(items);

        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Long orderId = orderService.processOrder(1L);

        assertEquals(1L, orderId);
        assertEquals(0, flashSaleProduct.getAvailable());
    }










}