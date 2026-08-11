package com.sana.cordeboheme.inventory_service.repository;

import com.sana.cordeboheme.inventory_service.entity.Inventory;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
  Optional<Inventory> findByProductIdAndDeletedFalse(UUID productId);

  boolean existsByProductIdAndDeletedFalse(UUID productId);
}
