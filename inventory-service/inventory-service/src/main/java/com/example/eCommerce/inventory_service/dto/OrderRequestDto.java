package com.example.eCommerce.inventory_service.dto;

import java.util.List;

public record OrderRequestDto(
        List<OrderRequestItemDto> items
) {
}
