package com.example.eCommerce.inventory_service.mapper;

import com.example.eCommerce.inventory_service.dto.ProductDto;
import com.example.eCommerce.inventory_service.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toProductDto(Product product);

    List<ProductDto> toProductDtoList(List<Product> products);
}
