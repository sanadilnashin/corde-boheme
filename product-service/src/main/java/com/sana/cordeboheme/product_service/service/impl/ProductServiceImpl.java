package com.sana.cordeboheme.product_service.service.impl;

import com.sana.cordeboheme.product_service.dto.request.createProductRequest;
import com.sana.cordeboheme.product_service.dto.response.ProductResponse;
import com.sana.cordeboheme.product_service.entity.Product;
import com.sana.cordeboheme.product_service.service.ProductService;
import java.util.List;

public class ProductServiceImpl implements ProductService {
  @Override
  public ProductResponse createProduct(createProductRequest request) {
    return null;
  }

  @Override
  public ProductResponse getProductById(Long Id) {
    return null;
  }

  @Override
  public List<Product> getAllProduct() {
    return null;
  }

  @Override
  public ProductResponse updateProduct(Long id, createProductRequest request) {
    return null;
  }

  @Override
  public void deleteProduct(Long id) {}
}
