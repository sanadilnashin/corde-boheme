package com.sana.cordeboheme.product_service.service.impl;

import com.sana.cordeboheme.product_service.dto.request.CreateProductRequest;
import com.sana.cordeboheme.product_service.dto.request.ProductSearchRequest;
import com.sana.cordeboheme.product_service.dto.response.ProductResponse;
import com.sana.cordeboheme.product_service.entity.Product;
import com.sana.cordeboheme.product_service.exception.ProductAlreadyExistsException;
import com.sana.cordeboheme.product_service.exception.ProductNotFoundException;
import com.sana.cordeboheme.product_service.mapper.ProductMapper;
import com.sana.cordeboheme.product_service.repository.ProductRepository;
import com.sana.cordeboheme.product_service.service.ProductService;
import com.sana.cordeboheme.product_service.specification.ProductSpecification;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    if (productRepository.existsByNameIgnoreCaseAndDeletedFalse(request.name())) {
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
    Product product = getProductById(id);
    product.setDescription(request.description());
    product.setName(request.name());
    product.setPrice(request.price());
    return productMapper.toResponse(product);
  }

  @Override
  public void deleteProduct(Long id) {
    Product product = getProductById(id);
    product.setDeleted(true);
    productRepository.save(product);
  }

  @Override
  public List<ProductResponse> createProducts(List<CreateProductRequest> request) {
    List<Product> productList = listToEntity(request);
    List<Product> productLists = productRepository.saveAll(productList);
    return listToResponse(productLists);
  }

  @Override
  public List<ProductResponse> getAllProductByPage(
      int page, int size, String sortBy, String direction) {
    Sort sort =
        direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);
    Page<Product> productList = productRepository.findAll(pageable);
    return productList.map(productMapper::toResponse).getContent();
  }

  @Override
  public Page<ProductResponse> productSearch(ProductSearchRequest request) {

    // Default sorting
    Sort sort = Sort.by("id").ascending();

    if (request.sort() != null && !request.sort().isBlank()) {

      String[] sortParts = request.sort().split(",");

      String field = sortParts[0];

      Sort.Direction direction =
          sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
              ? Sort.Direction.DESC
              : Sort.Direction.ASC;

      sort = Sort.by(direction, field);
    }

    Pageable pageable =
        PageRequest.of(
            request.page() == null ? 0 : request.page(),
            request.size() == null ? 10 : request.size(),
            sort);

    Specification<Product> specification =
        Specification.where(ProductSpecification.isNotDeleted())
            .and(ProductSpecification.hasName(request.name()))
            .and(ProductSpecification.hasMinPrice(request.minPrice()))
            .and(ProductSpecification.hasMaxPrice(request.maxPrice()));

    return productRepository.findAll(specification, pageable).map(productMapper::toResponse);
  }

  private List<Product> listToEntity(List<CreateProductRequest> request) {
    List<Product> productList = new java.util.ArrayList<>();
    for (CreateProductRequest productRequest : request) {
      productList.add(productMapper.toEntity(productRequest));
    }
    return productList;
  }

  private List<ProductResponse> listToResponse(List<Product> productLists) {
    List<ProductResponse> productResponses = new ArrayList<>();
    for (Product productRequest : productLists) {
      productResponses.add(productMapper.toResponse(productRequest));
    }
    return productResponses;
  }
}
