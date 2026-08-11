package com.sana.cordeboheme.product_service.repository;

import com.sana.cordeboheme.product_service.entity.Product;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository
    extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

  boolean existsByNameIgnoreCaseAndDeletedFalse(String name);
}
