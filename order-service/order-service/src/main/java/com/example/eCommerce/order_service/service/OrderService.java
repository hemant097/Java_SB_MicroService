package com.example.eCommerce.order_service.service;

import com.example.eCommerce.order_service.dto.OrderRequestDto;
import com.example.eCommerce.order_service.entity.Order;
import com.example.eCommerce.order_service.mapper.OrderMapper;
import com.example.eCommerce.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepo;

    public List<OrderRequestDto> getAllOrders(){
        log.info("Fetching all orders");
        return orderMapper.toOrderRequestDtoList(orderRepo.findAll());
    }

    public OrderRequestDto getOrderById(Long id){
        log.info("Fetching order with id: {}",id);
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("no order found with ID: "+id));
        return orderMapper.toOrderRequestDto(order);
    }
}
