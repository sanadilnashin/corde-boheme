package com.sana.cordeboheme.inventory_service.dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateInventoryRequest(
    @NotNull(message = "Product Id is required") UUID productId,
    @NotNull(message = "Available Quantity is required")
        @Min(value = 0, message = "Available quantity cannot be negative")
        Integer availableQuantity) {}
