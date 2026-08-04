package com.sana.cordeboheme.product_service.repository;

import com.sana.cordeboheme.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository
    extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

  boolean existsByNameIgnoreCaseAndDeletedFalse(String name);
}
