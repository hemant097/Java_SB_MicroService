package com.example.eCommerce.order_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequestDto(
        Long id,
        List<OrderRequestItemDto> items,
        BigDecimal totalPrice
) {
}
