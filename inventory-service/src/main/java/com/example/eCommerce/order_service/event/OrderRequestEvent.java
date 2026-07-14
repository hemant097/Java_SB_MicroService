package com.example.eCommerce.order_service.event;

import com.example.eCommerce.inventory_service.dto.OrderRequestItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor@NoArgsConstructor
public class OrderRequestEvent {
    Long orderId;
    List<OrderRequestItemDto> items;
}
