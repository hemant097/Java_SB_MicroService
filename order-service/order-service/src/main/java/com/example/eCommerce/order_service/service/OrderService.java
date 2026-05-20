package com.example.eCommerce.order_service.service;

import com.example.eCommerce.order_service.clients.InventoryOpenFeignClient;
import com.example.eCommerce.order_service.dto.CancelRequestDto;
import com.example.eCommerce.order_service.dto.CancelResponseDto;
import com.example.eCommerce.order_service.dto.OrderRequestDto;
import com.example.eCommerce.order_service.dto.OrderRequestItemDto;
import com.example.eCommerce.order_service.entity.Order;
import com.example.eCommerce.order_service.entity.OrderItem;
import com.example.eCommerce.order_service.entity.OrderStatus;
import com.example.eCommerce.order_service.mapper.OrderMapper;
import com.example.eCommerce.order_service.repository.OrderItemRepository;
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
    private final OrderItemRepository orderItemRepo;
    private final InventoryOpenFeignClient inventoryOpenFeignClient;


    public List<OrderRequestDto> getAllOrders(){
        log.info("Fetching all orders");
        return orderMapper.toOrderDtoList(orderRepo.findAll());
    }

    public OrderRequestDto getOrderById(Long id){
        log.info("Fetching order with id: {}",id);
        Order order = whetherOrderExists(id);
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

    public CancelResponseDto cancelOrder(CancelRequestDto cancelRequest){
        log.info("cancelling orders, and increasing inventory stock using inventory_OpenFeign_client");
        Long itemsRestocked = inventoryOpenFeignClient.increaseStocks(cancelRequest);

        Order order = whetherOrderExists(cancelRequest.orderId());

        for( OrderRequestItemDto itemToModify: cancelRequest.items()){
            OrderItem orderItem = whetherOrderItemExists(itemToModify.id());

            if(itemToModify.quantity() > orderItem.getQuantity())
                throw new RuntimeException("cannot remove more than ordered quantity");

            orderItemRepo.delete(orderItem);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepo.save(order);

        return new CancelResponseDto(cancelRequest.orderId(), itemsRestocked, order.getTotalPrice());

    }

    //TODO: create shipping service


    Order whetherOrderExists(Long orderId){
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("no order found with ID: "+orderId));
    }
    OrderItem whetherOrderItemExists(Long id){
        return orderItemRepo.findById(id).orElseThrow(() -> new RuntimeException("no order item exists "));
    }


}
