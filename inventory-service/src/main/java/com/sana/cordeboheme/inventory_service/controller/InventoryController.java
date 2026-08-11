package com.sana.cordeboheme.inventory_service.controller;

import com.sana.cordeboheme.inventory_service.dto.Request.CreateInventoryRequest;
import com.sana.cordeboheme.inventory_service.dto.Request.UpdateInventoryRequest;
import com.sana.cordeboheme.inventory_service.dto.Response.InventoryResponse;
import com.sana.cordeboheme.inventory_service.mapper.InventoryMapper;
import com.sana.cordeboheme.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
  private final InventoryService inventoryService;

  public InventoryController(InventoryService inventoryService, InventoryMapper inventoryMapper) {
    this.inventoryService = inventoryService;
  }

  @PostMapping
  InventoryResponse createInventory(@Valid @RequestBody CreateInventoryRequest request) {
    return inventoryService.createInventory(request);
  }

  @PutMapping("/{id}")
  InventoryResponse updateInventory(
      @PathVariable UUID id, @Valid @RequestBody UpdateInventoryRequest request) {
    return inventoryService.updateInventory(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteInventory(@PathVariable UUID id) {
    inventoryService.deleteInventory(id);
    return ResponseEntity.noContent().build();
  }
}
