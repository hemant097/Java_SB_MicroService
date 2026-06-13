package com.example.eCommerce.order_service.controller;

import com.example.eCommerce.order_service.dto.CancelRequestDto;
import com.example.eCommerce.order_service.dto.CancelResponseDto;
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
    public ResponseEntity<String> testOrder(@RequestHeader(name = "X-User-Id") String userId){
        return ResponseEntity.ok("Hello from order Service, !!, the user-id is: "+userId);
    }

    @PostMapping("create-order")
    public ResponseEntity<OrderRequestDto> createOrder(@RequestBody OrderRequestDto orderRequest){
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @PostMapping("cancel-order")
    public ResponseEntity<CancelResponseDto> cancelOrder(@RequestBody CancelRequestDto cancelRequest){
        log.info("Inside controller method");
        return ResponseEntity.ok(orderService.cancelOrder(cancelRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRequestDto> getOrderById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderRequestDto>> getAllOrders(){
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
