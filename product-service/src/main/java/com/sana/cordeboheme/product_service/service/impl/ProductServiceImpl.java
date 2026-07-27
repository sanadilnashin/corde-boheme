package com.sana.cordeboheme.product_service.service.impl;

import com.sana.cordeboheme.product_service.dto.request.CreateProductRequest;
import com.sana.cordeboheme.product_service.dto.response.ProductResponse;
import com.sana.cordeboheme.product_service.entity.Product;
import com.sana.cordeboheme.product_service.exception.ResourceAlreadyExistsException;
import com.sana.cordeboheme.product_service.exception.ResourceNotFoundException;
import com.sana.cordeboheme.product_service.mapper.ProductMapper;
import com.sana.cordeboheme.product_service.repository.ProductRepository;
import com.sana.cordeboheme.product_service.service.ProductService;
import java.util.List;
import java.util.Optional;
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
      throw new ResourceAlreadyExistsException("Product already created");
    }
    Product product = productMapper.toEntity(request);
    Product savedProduct = productRepository.save(product);
    return productMapper.toResponse(savedProduct);
  }

  @Override
  public Optional<Product> getProductById(Long Id) {
    Optional<Product> savedProduct = productRepository.findById(Id);
    if(savedProduct.isEmpty())
    {
      throw new ResourceNotFoundException("Product not found");
    }
    return savedProduct;
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
