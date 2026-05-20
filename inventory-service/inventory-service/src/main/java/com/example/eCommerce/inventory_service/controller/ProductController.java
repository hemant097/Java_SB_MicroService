package com.example.eCommerce.inventory_service.controller;

import com.example.eCommerce.inventory_service.clients.OrdersFeignClient;
import com.example.eCommerce.inventory_service.dto.CancelRequestDto;
import com.example.eCommerce.inventory_service.dto.OrderRequestDto;
import com.example.eCommerce.inventory_service.dto.ProductDto;
import com.example.eCommerce.inventory_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final RestClient restClient;
    private final DiscoveryClient discoveryClient;
    private final OrdersFeignClient ordersFeignClient;

    @GetMapping("/fetchOrders")
    public ResponseEntity<String> fetchOrdersFromOrderService(@RequestHeader("x-custom-header") String customHeader){
        log.info("Custom header value:{}",customHeader);

//        ServiceInstance orderService = discoveryClient.getInstances("order-service").getFirst();
//        return restClient
//                .get()
//                .uri(orderService.getUri()+"/orders/core/test")
//                .retrieve()
//                .toEntity(String.class);

        //using feign client
        return ResponseEntity.ok(ordersFeignClient.helloOrders());

    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllItems(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getSpecificItem(@PathVariable(name = "id") Long productId){
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PutMapping("reduce-stocks")
    public ResponseEntity<Double> reduceStocks(@RequestBody OrderRequestDto orderRequest){
        return ResponseEntity.ok(productService.reduceStocks(orderRequest));
    }

    @PutMapping("increase-stocks")
    public ResponseEntity<Long> increaseStocks(@RequestBody CancelRequestDto cancelRequest){
        return ResponseEntity.ok(productService.increaseStocks(cancelRequest));
    }
}
