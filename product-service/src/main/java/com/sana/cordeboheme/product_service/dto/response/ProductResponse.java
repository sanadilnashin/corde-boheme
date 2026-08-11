package com.sana.cordeboheme.product_service.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
    UUID id, String sku, String name, String description, BigDecimal price) {}
