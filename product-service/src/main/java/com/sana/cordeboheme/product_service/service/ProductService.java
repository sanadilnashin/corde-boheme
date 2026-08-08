package com.sana.cordeboheme.product_service.service;

import com.sana.cordeboheme.product_service.dto.request.CreateProductRequest;
import com.sana.cordeboheme.product_service.dto.request.ProductSearchRequest;
import com.sana.cordeboheme.product_service.dto.response.ProductResponse;
import com.sana.cordeboheme.product_service.entity.Product;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

public interface ProductService {
  ProductResponse createProduct(CreateProductRequest request);

  Product getProductById(UUID Id);

  List<Product> getAllProduct();

  ProductResponse updateProduct(UUID id, CreateProductRequest request);

  void deleteProduct(UUID id);

  List<ProductResponse> createProducts(List<CreateProductRequest> request);

  List<ProductResponse> getAllProductByPage(int page, int size, String sortBy, String direction);

  Page<ProductResponse> productSearch(ProductSearchRequest productSearchRequest);
}
