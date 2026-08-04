package com.sana.cordeboheme.product_service.dto.request;

import java.math.BigDecimal;

public record ProductSearchRequest(
    String name,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    Integer size,
    Integer page,
    String sort) {}
