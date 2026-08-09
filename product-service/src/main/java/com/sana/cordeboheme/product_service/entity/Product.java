package com.sana.cordeboheme.product_service.entity;

import com.sana.cordeboheme.common.config.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends BaseEntity {
  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, unique = true, length = 50)
  private String sku;

  private String name;
  private String description;
  private BigDecimal price;

  @Column(nullable = false)
  private Boolean deleted = false;
}
