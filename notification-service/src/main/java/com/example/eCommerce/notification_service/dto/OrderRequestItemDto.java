package com.example.eCommerce.notification_service.dto;

public record OrderRequestItemDto(
        Long id,
        Long productId,
        Integer quantity
) {
}