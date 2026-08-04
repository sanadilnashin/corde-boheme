package com.sana.cordeboheme.product_service.dto.response;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, BigDecimal price) {}
