package com.sana.cordeboheme.product_service.event;

import java.util.UUID;

public record ProductCreatedEvent(UUID productId, String sku) {}
