package com.example.eCommerce.inventory_service.dto;

import java.util.List;

public record CancelRequestDto(
        Long orderId,
        List<OrderRequestItemDto> items
) {
}
