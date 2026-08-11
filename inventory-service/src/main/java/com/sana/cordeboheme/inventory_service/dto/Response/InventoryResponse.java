package com.sana.cordeboheme.inventory_service.dto.Response;

import java.util.UUID;

public record InventoryResponse(
        UUID id, UUID productId, Integer availableQuantity, Integer reservedQuantity) {}
