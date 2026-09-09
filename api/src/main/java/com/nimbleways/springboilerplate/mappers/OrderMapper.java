package com.nimbleways.springboilerplate.mappers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    ProcessOrderResponse toProcessOrderResponse(Order order);
}