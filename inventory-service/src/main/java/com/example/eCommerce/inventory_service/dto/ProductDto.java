package com.example.eCommerce.inventory_service.dto;

public record ProductDto(
         Long id,
         String title,
         Integer stock,
         Double price
) { }
