package com.example.eCommerce.inventory_service.service;

import com.example.eCommerce.inventory_service.dto.ProductDto;
import com.example.eCommerce.inventory_service.entity.Product;
import com.example.eCommerce.inventory_service.mapper.ProductMapper;
import com.example.eCommerce.inventory_service.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepo;
    private final ProductMapper productMapper;


    public List<ProductDto> getAllProducts(){
        log.info("Fetching all inventory items");
        return productMapper.toProductDtoList(productRepo.findAll());
    }

    public ProductDto getProductById(Long productId){
        log.info("Fetching product with ID:{}",productId);
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("no such product found with this id:"+productId));
        return productMapper.toProductDto(product);
    }
}
