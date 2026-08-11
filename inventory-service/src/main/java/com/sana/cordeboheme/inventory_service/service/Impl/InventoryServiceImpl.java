package com.sana.cordeboheme.inventory_service.service.Impl;

import com.sana.cordeboheme.inventory_service.Exception.InventoryAlreadyExistsException;
import com.sana.cordeboheme.inventory_service.Exception.InventoryNotFoundException;
import com.sana.cordeboheme.inventory_service.dto.Request.CreateInventoryRequest;
import com.sana.cordeboheme.inventory_service.dto.Request.UpdateInventoryRequest;
import com.sana.cordeboheme.inventory_service.dto.Response.InventoryResponse;
import com.sana.cordeboheme.inventory_service.entity.Inventory;
import com.sana.cordeboheme.inventory_service.mapper.InventoryMapper;
import com.sana.cordeboheme.inventory_service.repository.InventoryRepository;
import com.sana.cordeboheme.inventory_service.service.InventoryService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {
  private final InventoryRepository inventoryRepository;
  private final InventoryMapper inventoryMapper;

  public InventoryServiceImpl(
      InventoryRepository inventoryRepository, InventoryMapper inventoryMapper) {
    this.inventoryRepository = inventoryRepository;
    this.inventoryMapper = inventoryMapper;
  }

  @Override
  public InventoryResponse createInventory(CreateInventoryRequest request) {
    if (inventoryRepository.existsByProductIdAndDeletedFalse(request.productId())) {
      throw new InventoryAlreadyExistsException("Inventory already created");
    }
    Inventory inventory = inventoryMapper.toEntity(request);
    Inventory savedInventory = inventoryRepository.save(inventory);
    return inventoryMapper.toResponse(savedInventory);
  }

  @Override
public InventoryResponse updateInventory(UUID productId, UpdateInventoryRequest request) {

    Inventory inventory = getInventoryByProductId(productId);

    inventory.setAvailableQuantity(request.availableQuantity());
    Inventory updatedInventory = inventoryRepository.save(inventory);

    return inventoryMapper.toResponse(updatedInventory);
  }

  @Override
  public Inventory getInventoryByProductId(UUID productId) {
    return inventoryRepository
        .findByProductIdAndDeletedFalse(productId)
        .orElseThrow(
            () ->
                new InventoryNotFoundException(
                    "Inventory already exists for product id:" + productId));
  }

  @Override
  public void deleteInventory(UUID id) {
    Inventory inventory = getInventoryByProductId(id);
    inventory.setDeleted(true);
    inventoryRepository.save(inventory);
  }
}
