package com.sana.cordeboheme.product_service.controller;

import com.sana.cordeboheme.product_service.dto.request.CreateProductRequest;
import com.sana.cordeboheme.product_service.dto.request.ProductSearchRequest;
import com.sana.cordeboheme.product_service.dto.response.ProductResponse;
import com.sana.cordeboheme.product_service.entity.Product;
import com.sana.cordeboheme.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  @Operation(
      hidden = true,
      summary = "Create Product",
      description = "Creates a new handmade product.")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Product created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
    return productService.createProduct(request);
  }

  @GetMapping(params = "id")
  @Operation(summary = "get product by ID", description = "fetch product by ID.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Product fetched successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "404", description = "Product not found")
  })
  Product getProductById(@RequestParam Long id) {
    return productService.getProductById(id);
  }

  @GetMapping
  @Operation(summary = "Fetch All Products", description = "fetch products")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Product fetched successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "404", description = "Product not found")
  })
  List<Product> getProducts() {
    return productService.getAllProduct();
  }

  @PostMapping("/allProduct")
  @Operation(
      hidden = true,
      summary = "Create Multiple Products",
      description = "Create a list of products in bulk")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Products created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid product list or request data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
  })
  List<ProductResponse> createProducts(
      @RequestBody @NotEmpty(message = "Product list cannot be empty")
          List<@Valid CreateProductRequest> request) {
    return productService.createProducts(request);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update Product", description = "Update an existing product by ID")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Product updated successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "404", description = "Product not found")
  })
  ProductResponse updateProduct(
      @PathVariable Long id, @Valid @RequestBody CreateProductRequest request) {
    return productService.updateProduct(id, request);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete Product", description = "Delete an existing product by ID")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid product ID supplied"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Forbidden"),
    @ApiResponse(responseCode = "404", description = "Product not found")
  })
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/all")
  @Operation(
      summary = "Fetch All Products",
      description = "Retrieve all products with pagination and sorting")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Products fetched successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid pagination or sorting parameters"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  List<ProductResponse> getAllProduct(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "0") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "desc") String direction) {
    return productService.getAllProductByPage(page, size, sortBy, direction);
  }

  @GetMapping("/search")
  @Operation(
      summary = "Search Products",
      description = "Search products with filters and pagination")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Products fetched successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "404", description = "No products found")
  })
  List<ProductResponse> productSearch(ProductSearchRequest productSearchRequest) {
    return productService.productSearch(productSearchRequest).getContent();
  }
}
