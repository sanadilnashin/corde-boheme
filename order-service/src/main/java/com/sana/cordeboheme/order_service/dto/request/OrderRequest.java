package com.sana.cordeboheme.order_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {

    @NotNull(message = "customerId can't be null")
    private UUID customerId;

    @NotEmpty(message = "item list can't be empty")
    @Valid
    private List<OrderItemRequest> items;
}
