package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.contollers.OrderController;
import com.nimbleways.springboilerplate.services.implementations.OrderService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Specify the controller class you want to test
// This indicates to spring boot to only load UsersController into the context
// Which allows a better performance and needs to do less mocks
@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void processOrderShouldReturn() throws Exception {
        Mockito.when(orderService.processOrder(99L)).thenReturn(99L);
        mockMvc.perform(post("/orders/{orderId}/processOrder", 99L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(99)));

        Mockito.when(orderService.processOrder(400L)).thenReturn(-1L);
        mockMvc.perform(post("/orders/{orderId}/processOrder", 400L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(-1)));
    }
}
