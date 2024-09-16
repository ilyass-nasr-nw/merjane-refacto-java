package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.helpers.ProductHelper;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.strategies.product.ProductTypeStrategy;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


@UnitTest
@ExtendWith(SpringExtension.class)
class ProductServiceTest {


    @Mock
    private List<ProductTypeStrategy> strategies;

    @Mock
    private ProductTypeStrategy productTypeStrategy;


    @InjectMocks
    private ProductService service;


    @Test
    public void testProcessProductWillProcessNormalProduct(){

        //given
        var product = ProductHelper.createProduct(ProductHelper.defaultProductValues(new HashMap<>()));
        Mockito.when(strategies.stream()).thenReturn(Stream.of(productTypeStrategy));
        Mockito.when(productTypeStrategy.getProductType()).thenReturn(ProductType.NORMAL);

        //when
        service.processProduct(product);

        //then
        Mockito.verify(productTypeStrategy).process(product);

    }

    @Test
    public void testProcessProductWillProcessSeasonalProduct(){

        //given
        HashMap<String, Object> productValues = new HashMap<>();
        productValues.put("type", ProductType.SEASONAL);
        var product = ProductHelper.createProduct(ProductHelper.defaultProductValues(productValues));
        Mockito.when(strategies.stream()).thenReturn(Stream.of(productTypeStrategy));
        Mockito.when(productTypeStrategy.getProductType()).thenReturn(ProductType.SEASONAL);

        //when
        service.processProduct(product);

        //then
        Mockito.verify(productTypeStrategy).process(product);

    }


    @Test
    public void testProcessProductWillProcessExpirableProduct(){
        //given
        HashMap<String, Object> productValues = new HashMap<>();
        productValues.put("type", ProductType.EXPIRABLE);
        var product = ProductHelper.createProduct(ProductHelper.defaultProductValues(productValues));
        Mockito.when(strategies.stream()).thenReturn(Stream.of(productTypeStrategy));
        Mockito.when(productTypeStrategy.getProductType()).thenReturn(ProductType.EXPIRABLE);
        //when
        service.processProduct(product);
        //then
        Mockito.verify(productTypeStrategy).process(product);

    }


    @Test
    public void testProcessProductWillProcessFlashSaleProduct(){
        //given
        HashMap<String, Object> productValues = new HashMap<>();
        productValues.put("type", ProductType.FLASHSALE);
        var product = ProductHelper.createProduct(ProductHelper.defaultProductValues(productValues));
        Mockito.when(strategies.stream()).thenReturn(Stream.of(productTypeStrategy));
        Mockito.when(productTypeStrategy.getProductType()).thenReturn(ProductType.FLASHSALE);
        //when
        service.processProduct(product);
        //then
        Mockito.verify(productTypeStrategy).process(product);

    }
}