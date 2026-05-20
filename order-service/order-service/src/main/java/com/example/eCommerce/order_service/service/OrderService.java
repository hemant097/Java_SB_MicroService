package com.example.eCommerce.order_service.service;

import com.example.eCommerce.order_service.clients.InventoryOpenFeignClient;
import com.example.eCommerce.order_service.dto.OrderRequestDto;
import com.example.eCommerce.order_service.entity.Order;
import com.example.eCommerce.order_service.entity.OrderItem;
import com.example.eCommerce.order_service.entity.OrderStatus;
import com.example.eCommerce.order_service.mapper.OrderMapper;
import com.example.eCommerce.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
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
    private final InventoryOpenFeignClient inventoryOpenFeignClient;


    public List<OrderRequestDto> getAllOrders(){
        log.info("Fetching all orders");
        return orderMapper.toOrderDtoList(orderRepo.findAll());
    }

    public OrderRequestDto getOrderById(Long id){
        log.info("Fetching order with id: {}",id);
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("no order found with ID: "+id));
        return orderMapper.toOrderRequestDto(order);
    }

//    @Retry(name = "inventoryRetry", fallbackMethod ="createOrderFallback" )
    @CircuitBreaker(name = "inventoryCircuitBreaker", fallbackMethod = "createOrderFallback")
//    @RateLimiter(name ="inventoryRateLimiter", fallbackMethod = "createOrderFallback")
    public OrderRequestDto createOrder(OrderRequestDto orderRequest){
        log.info("creating orders, and reducing inventory stock using inventory_OpenFeign_client");
        Double totalPrice = inventoryOpenFeignClient.reduceStocks(orderRequest);

        Order order = orderMapper.toOrder(orderRequest);

        for( OrderItem orderItem:order.getItems()) {
            orderItem.setOrder(order);
        }

        order.setTotalPrice(totalPrice);
        order.setOrderStatus(OrderStatus.CONFIRMED);

        Order savedOrder = orderRepo.save(order);
        return orderMapper.toOrderRequestDto(savedOrder);
    }

    public OrderRequestDto  createOrderFallback(OrderRequestDto orderRequestDto, Throwable throwable){
        log.error("Fallback occurred due to: {}",throwable.getMessage());
        return new OrderRequestDto();
    }


}
