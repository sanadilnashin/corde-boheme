package com.sana.cordeboheme.product_service.repository;

import com.sana.cordeboheme.product_service.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findByName(String name);

  Boolean existsByName(String name);
}
