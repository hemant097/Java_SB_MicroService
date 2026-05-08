package com.example.eCommerce.inventory_service.controller;

import com.example.eCommerce.inventory_service.dto.ProductDto;
import com.example.eCommerce.inventory_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final RestClient restClient;
    private final DiscoveryClient discoveryClient;

    @GetMapping("/fetchOrders")
    public ResponseEntity<String> fetchOrdersFromOrderService(){
        ServiceInstance orderService = discoveryClient.getInstances("order-service").getFirst();

        return restClient
                .get()
                .uri(orderService.getUri()+"/api/v1/orders/test")
                .retrieve()
                .toEntity(String.class);
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllItems(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getSpecificItem(@PathVariable(name = "id") Long productId){
        return ResponseEntity.ok(productService.getProductById(productId));
    }
}
