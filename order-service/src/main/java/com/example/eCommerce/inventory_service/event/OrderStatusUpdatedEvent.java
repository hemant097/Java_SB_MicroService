package com.example.eCommerce.inventory_service.event;

import com.example.eCommerce.order_service.entity.OrderStatus;


public record OrderStatusUpdatedEvent (
        Long orderId,
     Double totalPrice,
     OrderStatus orderStatus
    )
{}