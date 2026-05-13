package com.example.eCommerce.order_service.controller;

import com.example.eCommerce.order_service.dto.OrderRequestDto;
import com.example.eCommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
@Slf4j
public class OrderController {

    private final OrderService orderService;


    @GetMapping("/test")
    public ResponseEntity<String> testOrder(){
        return ResponseEntity.ok("Hello from order Service, !!");
    }

    @PostMapping("create-order")
    public ResponseEntity<OrderRequestDto> createOrder(@RequestBody OrderRequestDto orderRequest){
        log.info("creating orders, and reducing inventory stock using inventory Open Feign client, from order-service");
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRequestDto> getOrderById(@PathVariable Long id){
        log.info("Fetching order with ID:{} via controller",id);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderRequestDto>> getAllOrders(){
        log.info("Fetching all orders via controller");
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
