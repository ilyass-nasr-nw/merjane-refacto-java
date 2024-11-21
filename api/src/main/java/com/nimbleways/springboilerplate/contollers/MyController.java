package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.ProcessOrder;
import com.nimbleways.springboilerplate.services.IProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class MyController {

    private  final IProductService productService;

    @PostMapping("processOrder")
    @ResponseStatus(HttpStatus.OK)
    public ProcessOrder processOrder(@RequestBody ProcessOrder processOrder) {

        return productService.processOrder(processOrder);
    }
}
