package com.example.eCommerce.order_service.dto;

public record OrderRequestItemDto(
        Long id,
        Long productId,
        Integer quantity
) {
}