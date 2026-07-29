package com.sana.cordeboheme.product_service.controller;

import com.sana.cordeboheme.product_service.dto.request.CreateProductRequest;
import com.sana.cordeboheme.product_service.dto.response.ProductResponse;
import com.sana.cordeboheme.product_service.entity.Product;
import com.sana.cordeboheme.product_service.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  ProductResponse createProduct(@RequestBody CreateProductRequest request) {
    return productService.createProduct(request);
  }

  @GetMapping(params = "id")
  Product getProductById(@RequestParam Long id) {
    return productService.getProductById(id);
  }

  @GetMapping
  List<Product> getProducts() {
    return productService.getAllProduct();
  }
}
