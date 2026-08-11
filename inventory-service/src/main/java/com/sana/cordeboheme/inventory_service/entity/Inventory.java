package com.sana.cordeboheme.inventory_service.entity;

import com.sana.cordeboheme.common.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

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
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false,updatable = false)
    private UUID productId;

    private Integer availableQuantity;

    private Integer reservedQuantity;

    @Column(nullable = false)
    private Boolean deleted = false;
}
