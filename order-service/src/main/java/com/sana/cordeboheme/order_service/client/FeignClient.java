package com.sana.cordeboheme.order_service.client;

import com.sana.cordeboheme.order_service.client.dto.ProductResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@org.springframework.cloud.openfeign.FeignClient(
    name = "Product-service",
    url = "${product.service.url}")
public interface FeignClient {

  @GetMapping("/api/v1/products")
  ProductResponse getProductById(@RequestParam UUID id);
}
