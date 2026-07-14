package com.example.eCommerce.inventory_service.service;

import com.example.eCommerce.inventory_service.dto.OrderRequestDto;
import com.example.eCommerce.inventory_service.enums.OrderStatus;
import com.example.eCommerce.order_service.event.OrderRequestEvent;
import com.example.eCommerce.inventory_service.event.OrderStatusUpdatedEvent;
import com.example.eCommerce.inventory_service.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ProductRepository productRepo;

    private final KafkaTemplate<String, OrderStatusUpdatedEvent> kafkaTemplate;
    private final ProductService productService;

    @Value("${kafka.topic.order-status-updated-topic}")
    private String KAFKA_ORDER_STATUS_UPDATED_TOPIC;

    @KafkaListener(topics = "order-created-topic")
    private void reduceStocksViaKafka(OrderRequestEvent orderRequestEvent){

        log.info("Via kafka Reducing the inventory stocks for :{} items",orderRequestEvent.getItems().size());
        Long orderId = orderRequestEvent.getOrderId();

        OrderRequestDto orderRequestDto = new OrderRequestDto(orderRequestEvent.getItems());


        double totalPrice;
        OrderStatus orderStatus;
        try{
            totalPrice = productService.reduceStocks(orderRequestDto);
            orderStatus = OrderStatus.SHIPPED;
        }
        catch (Exception e){
            totalPrice = 0.0;
            orderStatus = OrderStatus.OUT_OF_STOCK;
        }

        kafkaTemplate.send(KAFKA_ORDER_STATUS_UPDATED_TOPIC,new OrderStatusUpdatedEvent(orderId, totalPrice,orderStatus));

    }


}
