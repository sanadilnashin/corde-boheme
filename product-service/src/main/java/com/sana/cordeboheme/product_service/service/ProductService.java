package com.sana.cordeboheme.product_service.service;

import com.sana.cordeboheme.product_service.dto.request.CreateProductRequest;
import com.sana.cordeboheme.product_service.dto.response.ProductResponse;
import com.sana.cordeboheme.product_service.entity.Product;
import java.util.List;

public interface ProductService {
  ProductResponse createProduct(CreateProductRequest request);

  ProductResponse getProductById(Long Id);

  List<Product> getAllProduct();

  ProductResponse updateProduct(Long id, CreateProductRequest request);

  void deleteProduct(Long id);
}
