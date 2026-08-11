package com.sana.cordeboheme.inventory_service.service;

import com.sana.cordeboheme.inventory_service.dto.Request.CreateInventoryRequest;
import com.sana.cordeboheme.inventory_service.dto.Request.UpdateInventoryRequest;
import com.sana.cordeboheme.inventory_service.dto.Response.InventoryResponse;
import com.sana.cordeboheme.inventory_service.entity.Inventory;

import java.util.UUID;

public interface InventoryService {

  InventoryResponse createInventory(CreateInventoryRequest request);

  InventoryResponse updateInventory(UUID productId, UpdateInventoryRequest request);

  Inventory getInventoryByProductId(UUID Id);

  void deleteInventory(UUID id);
}
