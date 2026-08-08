package com.sana.cordeboheme.inventory_service.entity;

import com.sana.cordeboheme.common.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "inventory")
public class Inventory extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long productId;

  private Integer availableQuantity;

  private Integer reservedQuantity;

  @Column(nullable = false)
  private Boolean deleted = false;
}
