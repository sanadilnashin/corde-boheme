package com.sana.cordeboheme.product_service.mapper;

import com.sana.cordeboheme.product_service.dto.request.CreateProductRequest;
import com.sana.cordeboheme.product_service.dto.response.ProductResponse;
import com.sana.cordeboheme.product_service.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

  public Product toEntity(CreateProductRequest request) {
    return Product.builder()
        .description(request.description())
        .name(request.name())
        .price(request.price())
        .sku(request.sku())
        .deleted(false)
        .build();
  }

  public ProductResponse toResponse(Product product) {
    return new ProductResponse(
        product.getId(),
        product.getSku(),
        product.getName(),
        product.getDescription(),
        product.getPrice());
  }
}
