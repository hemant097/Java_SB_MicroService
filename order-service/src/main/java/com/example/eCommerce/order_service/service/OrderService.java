package com.example.eCommerce.order_service.service;

import com.example.eCommerce.order_service.clients.InventoryOpenFeignClient;
import com.example.eCommerce.order_service.dto.*;
import com.example.eCommerce.order_service.entity.Order;
import com.example.eCommerce.order_service.entity.OrderItem;
import com.example.eCommerce.order_service.entity.OrderStatus;
import com.example.eCommerce.order_service.event.OrderRequestEvent;
import com.example.eCommerce.inventory_service.event.OrderStatusUpdatedEvent;
import com.example.eCommerce.order_service.mapper.OrderMapper;
import com.example.eCommerce.order_service.repository.OrderItemRepository;
import com.example.eCommerce.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, OrderRequestEvent> kafkaTemplate;


    @Value("${kafka.topic.order-created-topic}")
    private String KAFKA_ORDER_CREATED_TOPIC;


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
    public InitialOrderResponse createOrder(OrderRequestDto orderRequest){
        log.info("creating orders, and reducing inventory stock using inventory_OpenFeign_client");
        Order order = orderMapper.toOrder(orderRequest);
        order.setOrderStatus(OrderStatus.PENDING);

        for( OrderItem orderItem:order.getItems()) {
            orderItem.setOrder(order);
        }

        Order savedOrder = orderRepo.save(order);

        OrderRequestEvent orderRequestEvent = new OrderRequestEvent(savedOrder.getId(),orderRequest.items());
        kafkaTemplate.send(KAFKA_ORDER_CREATED_TOPIC,orderRequestEvent);

        List<OrderRequestItemDto> itemsWithIds = orderMapper.toOrderRequestItemDtoList(savedOrder.getItems());

        return new InitialOrderResponse(savedOrder.getId(),itemsWithIds, OrderStatus.PENDING);
    }


    @KafkaListener(topics = "order-status-updated-topic")
    public OrderRequestDto handleOrderStatus(OrderStatusUpdatedEvent orderStatusUpdatedEvent){

        System.out.println("inside handlerOrderStatus, publishing" + orderStatusUpdatedEvent );

        Order order = orderRepo.findById(orderStatusUpdatedEvent.orderId())
                .orElseThrow(()-> new RuntimeException("something bad happened"));
        order.setTotalPrice(orderStatusUpdatedEvent.totalPrice());
        order.setOrderStatus(orderStatusUpdatedEvent.orderStatus());

        Order updatedOrder = orderRepo.save(order);

        return orderMapper.toOrderRequestDto(updatedOrder);

    }

    public OrderRequestDto createOrderFallback(OrderRequestDto orderRequestDto, Throwable throwable){
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
