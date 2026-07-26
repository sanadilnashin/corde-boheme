package com.sana.cordeboheme.product_service.mapper;

import com.sana.cordeboheme.product_service.dto.request.createProductRequest;
import com.sana.cordeboheme.product_service.dto.response.ProductResponse;
import com.sana.cordeboheme.product_service.entity.Product;

public class ProductMapper {

  Product toEntity(createProductRequest request) {
    return Product.builder()
        .description(request.description())
        .name(request.name())
        .quantity(request.quantity())
        .price(request.price())
        .build();
  }

  ProductResponse toResponse(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.getQuantity());
  }
}
