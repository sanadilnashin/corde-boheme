package com.sana.cordeboheme.inventory_service.repository;

import com.sana.cordeboheme.inventory_service.entity.Inventory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
  Optional<Inventory> findByProductIdAndDeletedFalse(Long productId);

  boolean existsByProductIdAndDeletedFalse(Long productId);
}
