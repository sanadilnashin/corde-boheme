package com.sana.cordeboheme.common.event;

import java.util.UUID;

public record ProductCreatedEvent(UUID productId, String sku) {}
