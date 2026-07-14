package com.example.eCommerce.order_service.dto;

import com.example.eCommerce.order_service.entity.OrderStatus;

import java.util.List;


public record OrderRequestDto(
        Long id,
        List<OrderRequestItemDto> items,
        Double totalPrice,
        OrderStatus orderStatus
) {
    public OrderRequestDto() {
        this(null,null,null,null);
    }
}
