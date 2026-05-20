package com.example.eCommerce.order_service.dto;

public record CancelResponseDto(
        Long orderOd,
        Long itemsCancelled,
        Double amountReturned
) {
}
