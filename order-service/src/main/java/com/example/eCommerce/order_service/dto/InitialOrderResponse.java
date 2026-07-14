package com.example.eCommerce.order_service.dto;

import com.example.eCommerce.order_service.entity.OrderStatus;

import java.util.List;

public record InitialOrderResponse(
        Long orderId,
        List<OrderRequestItemDto> items,
        OrderStatus orderStatus
) {
}
