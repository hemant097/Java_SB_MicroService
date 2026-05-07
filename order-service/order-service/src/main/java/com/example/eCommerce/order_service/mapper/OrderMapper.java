package com.example.eCommerce.order_service.mapper;

import com.example.eCommerce.order_service.dto.OrderRequestDto;
import com.example.eCommerce.order_service.entity.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderRequestDto toOrderRequestDto(Order order);

    List<OrderRequestDto> toOrderRequestDtoList(List<Order> orders);
}
