package com.sana.cordeboheme.inventory_service.dto.Response;

public record InventoryResponse(
    Long id, Long productId, Integer availableQuantity, Integer reservedQuantity) {}
