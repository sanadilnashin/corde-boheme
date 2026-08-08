package com.sana.cordeboheme.inventory_service.mapper;

import com.sana.cordeboheme.inventory_service.dto.Request.CreateInventoryRequest;
import com.sana.cordeboheme.inventory_service.dto.Response.InventoryResponse;
import com.sana.cordeboheme.inventory_service.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {
  public Inventory toEntity(CreateInventoryRequest request) {

    return Inventory.builder()
        .productId(request.productId())
        .availableQuantity(request.availableQuantity())
        .reservedQuantity(0)
        .deleted(false)
        .build();
  }

  public InventoryResponse toResponse(Inventory inventory) {

    return new InventoryResponse(
        inventory.getId(),
        inventory.getProductId(),
        inventory.getAvailableQuantity(),
        inventory.getReservedQuantity());
  }
}
