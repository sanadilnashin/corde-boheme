package com.sana.cordeboheme.product_service.dto.request;

import java.math.BigDecimal;

public record CreateProductRequest(
    String name, String description, BigDecimal price, Integer quantity) {}
