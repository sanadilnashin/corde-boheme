package com.sana.cordeboheme.product_service.dto.response;

import java.math.BigDecimal;

public record createProductResponse(
    Long id, String name, String description, BigDecimal price, Integer quantity) {}
