package com.example.eCommerce.inventory_service.event;


import com.example.eCommerce.notification_service.enums.OrderStatus;

public record OrderStatusUpdatedEvent(
        Long orderId,
     Double totalPrice,
     OrderStatus orderStatus
    )
{}