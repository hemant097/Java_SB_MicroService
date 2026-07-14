package com.example.eCommerce.notification_service.consumer;

import com.example.eCommerce.notification_service.dto.OrderRequestItemDto;
import com.example.eCommerce.inventory_service.event.OrderStatusUpdatedEvent;
import com.example.eCommerce.order_service.event.OrderRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {


    @KafkaListener(topics = "order-created-topic")
    private void reduceStocksViaKafka(OrderRequestEvent orderRequestEvent){

        StringBuilder sb = new StringBuilder();

        for(int i=0; i< orderRequestEvent.getItems().size(); i++){
            OrderRequestItemDto item = orderRequestEvent.getItems().get(i);
            sb.append(i + 1)
                    .append("-> productId: ").append(item.productId())
                    .append(" quantity: ").append(item.quantity())
                    .append("\n");
        }

        log.info("New notification -> For orderId: {} Reducing the inventory stocks for :{} items",orderRequestEvent.getOrderId()
                ,orderRequestEvent.getItems().size());
        log.info("The items are as follows {}",sb);
    }

    @KafkaListener(topics = "order-status-updated-topic")
    private void reduceStocksViaKafka(OrderStatusUpdatedEvent statusUpdatedEvent){

        log.info("New notification -> For orderId:{} The total price is {}, and order status is :{} ",statusUpdatedEvent.orderId()
                , statusUpdatedEvent.totalPrice(), statusUpdatedEvent.orderStatus());
    }


}
