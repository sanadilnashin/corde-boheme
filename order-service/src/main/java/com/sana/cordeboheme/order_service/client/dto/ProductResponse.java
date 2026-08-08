package com.sana.cordeboheme.order_service.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(UUID id, String name, String description, BigDecimal price) {}
