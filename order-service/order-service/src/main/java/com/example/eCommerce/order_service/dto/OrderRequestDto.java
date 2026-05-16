package com.example.eCommerce.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


public record OrderRequestDto(
        Long id,
        List<OrderRequestItemDto> items,
        BigDecimal totalPrice
) {
    public OrderRequestDto() {
        this(null,List.of(),BigDecimal.ZERO);
    }
}
