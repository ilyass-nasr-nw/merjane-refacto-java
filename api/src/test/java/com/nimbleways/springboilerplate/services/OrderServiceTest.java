package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.helpers.OrderHelpers;
import com.nimbleways.springboilerplate.helpers.ProductHelper;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;


@UnitTest
@ExtendWith(SpringExtension.class)
public class OrderServiceTest {


    @Mock
    private OrderRepository orderRepositoryMock;

    @Mock
    private ProductService productServiceMock;
    @InjectMocks
    private OrderService service;

    @Test
    public void testProcessOrderWillThrowOrderNotFoundExceptionIfOrderNotFound() {
        //given
        final Long orderId = 1L;
        Mockito.when(orderRepositoryMock.findById(orderId)).thenReturn(Optional.empty());
        //when
        Assertions.assertThrowsExactly(OrderNotFoundException.class, () -> service.processOrder(orderId));
        //then
        Mockito.verify(orderRepositoryMock, Mockito.times(1)).findById(orderId);

    }

    @Test
    public void testProcessOrderWillThrowIllegalArgumentExceptionIfProductListIsNull() {
        //given
        final Long orderId = 1L;
        var order = OrderHelpers.buildOrder(null);
        Mockito.when(orderRepositoryMock.findById(orderId)).thenReturn(Optional.of(order));
        //when
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> service.processOrder(orderId));
        //then
        Mockito.verify(orderRepositoryMock, Mockito.times(1)).findById(orderId);

    }

    @Test
    public void testProcessOrderWillThrowIllegalArgumentExceptionIfProductListIsEmpty() {
        //given
        final Long orderId = 1L;
        var order = OrderHelpers.buildOrder(new HashSet<>());
        Mockito.when(orderRepositoryMock.findById(orderId)).thenReturn(Optional.of(order));
        //when
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> service.processOrder(orderId));
        //then
        Mockito.verify(orderRepositoryMock, Mockito.times(1)).findById(orderId);
    }


    @Test
    public void testProcessOrderWillProcessProductsIfProductListIsNotEmpty() {
        //given

        HashMap<String, Object> strawberryValues = new HashMap<>();
        strawberryValues.put("type", ProductType.SEASONAL);
        strawberryValues.put("name", "strawberry");
        strawberryValues.put("seasonStartDate", LocalDate.now().minusDays(10));
        strawberryValues.put("seasonEndDate", LocalDate.now().plusMonths(2));


        HashMap<String, Object> cheeseValues = new HashMap<>();
        cheeseValues.put("type", ProductType.EXPIRABLE);
        cheeseValues.put("name", "Kroon edam 1.7 kg");
        cheeseValues.put("expiryDate", LocalDate.now().plusMonths(10));


        HashMap<String, Object> monitorValues = new HashMap<>();
        monitorValues.put("type", ProductType.NORMAL);
        monitorValues.put("name", "Samsung Odyssey Qled 49");


        HashMap<String, Object> dysonValues = new HashMap<>();
        dysonValues.put("type", ProductType.FLASHSALE);
        dysonValues.put("name", "Dyson v15");
        dysonValues.put("flashSaleStartDate", LocalDate.now().minusDays(1));
        dysonValues.put("flashSaleEndDate", LocalDate.now().plusMonths(1));


        var strawberryProduct = ProductHelper.createProduct(strawberryValues);
        var cheeseProduct = ProductHelper.createProduct(cheeseValues);
        var monitorProduct = ProductHelper.createProduct(monitorValues);
        var dysonProduct = ProductHelper.createProduct(dysonValues);


        Set<Product> products = new HashSet<>();

        products.add( strawberryProduct);
        products.add( cheeseProduct);
        products.add( monitorProduct);
        products.add( dysonProduct);

        final Long orderId = 1L;
        var order = OrderHelpers.buildOrder(products);
        Mockito.when(orderRepositoryMock.findById(orderId)).thenReturn(Optional.of(order));
        //when
         var result = service.processOrder(orderId);
        //then

        Mockito.verify(orderRepositoryMock, Mockito.times(1)).findById(orderId);
        Mockito.verify(productServiceMock, Mockito.times(4)).processProduct(Mockito.any());
    }

}