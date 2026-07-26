package com.sana.cordeboheme.product_service.service;

import com.sana.cordeboheme.product_service.dto.request.createProductRequest;
import com.sana.cordeboheme.product_service.dto.response.ProductResponse;
import com.sana.cordeboheme.product_service.entity.Product;
import java.util.List;

public interface ProductService {
  ProductResponse createProduct(createProductRequest request);

  ProductResponse getProductById(Long Id);

  List<Product> getAllProduct();

  ProductResponse updateProduct(Long id, createProductRequest request);

  void deleteProduct(Long id);
}
