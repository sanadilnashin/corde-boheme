package com.sana.cordeboheme.product_service.service.impl;

import com.sana.cordeboheme.product_service.dto.request.CreateProductRequest;
import com.sana.cordeboheme.product_service.dto.response.ProductResponse;
import com.sana.cordeboheme.product_service.entity.Product;
import com.sana.cordeboheme.product_service.exception.ProductAlreadyExistsException;
import com.sana.cordeboheme.product_service.exception.ProductNotFoundException;
import com.sana.cordeboheme.product_service.mapper.ProductMapper;
import com.sana.cordeboheme.product_service.repository.ProductRepository;
import com.sana.cordeboheme.product_service.service.ProductService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.productMapper = productMapper;
  }

  @Override
  public ProductResponse createProduct(CreateProductRequest request) {
    if (productRepository.existsByName(request.name())) {
      throw new ProductAlreadyExistsException("Product already created");
    }
    Product product = productMapper.toEntity(request);
    Product savedProduct = productRepository.save(product);
    return productMapper.toResponse(savedProduct);
  }

  @Override
  public Product getProductById(Long Id) {

    return productRepository
        .findById(Id)
        .orElseThrow(() -> new ProductNotFoundException("Product not found with id " + Id));
  }

  @Override
  public List<Product> getAllProduct() {
    return productRepository.findAll();
  }

  @Override
  public ProductResponse updateProduct(Long id, CreateProductRequest request) {
    return null;
  }

  @Override
  public void deleteProduct(Long id) {}
}
