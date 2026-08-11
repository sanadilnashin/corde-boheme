package com.sana.cordeboheme.inventory_service.event;

import java.util.UUID;

public record ProductCreatedEvent(UUID productId, String sku) {}
