package com.example.eCommerce.inventory_service.dto;

public record OrderRequestItemDto(
        Long productId,
        Integer quantity
) {
}
