package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.businessservice.IOrderBusinessService;
import com.nimbleways.springboilerplate.businessservice.IProductBusinessService;
import com.nimbleways.springboilerplate.dto.ProcessOrder;
import com.nimbleways.springboilerplate.dto.enums.ProductTypeEnum;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.IProductService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    IProductService productService;

    @Mock
    IProductBusinessService productBusinessService;

    @Mock
    IOrderBusinessService orderBusinessService;

    @Mock
    NotificationService notificationService;

    Product product;
    ProcessOrder processOrder;
    Order order;

    @BeforeEach
    public  void setUp(){
        productService=new ProductService(productBusinessService,orderBusinessService,notificationService);
        product=new Product(1L,0,120, ProductTypeEnum.NORMAL,"ABCD", null,null,null);
        processOrder=new ProcessOrder(2L);
        order=new Order(2L, Set.of(product));

    }

    @Test
    void should_process_order_case_normal(){
        when(orderBusinessService.findById(2L)).thenReturn(order);

        var result=productService.processOrder(processOrder);

        Assertions.assertEquals(result.id(),2L);
        Assertions.assertEquals(product.getAvailable(),119);
    }

}