package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.contollers.OrdersController;
import com.nimbleways.springboilerplate.config.MessageSourceConfig;
import com.nimbleways.springboilerplate.config.GlobalExceptionHandler;
import com.nimbleways.springboilerplate.exceptions.BusinessException;
import com.nimbleways.springboilerplate.exceptions.TechnicalException;
import com.nimbleways.springboilerplate.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private ProductService productService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new OrdersController(productService))
                .setControllerAdvice(new GlobalExceptionHandler(new MessageSourceConfig().messageSource()))
                .build();
    }

    @Test
    void returnsBadRequestForBusinessException() throws Exception {
        when(productService.processOrder(1L)).thenThrow(new BusinessException("error.order.not.found"));

        mockMvc.perform(post("/orders/1/processOrder"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Order not found."));
    }

    @Test
    void hidesTechnicalExceptionDetails() throws Exception {
        when(productService.processOrder(1L)).thenThrow(new TechnicalException("technical.unexpected"));

        mockMvc.perform(post("/orders/1/processOrder"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected technical error occurred."));
    }
}
