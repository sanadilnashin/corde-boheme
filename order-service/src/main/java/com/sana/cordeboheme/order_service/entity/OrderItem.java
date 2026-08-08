package com.sana.cordeboheme.order_service.entity;

import com.sana.cordeboheme.common.config.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "Order_Item")
public class OrderItem extends BaseEntity {
  @Id
  @GeneratedValue
  @Column(nullable = false)
  private UUID orderItemId;

  // order item can not exist without an order
  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(nullable = false)
  private UUID productId;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false, precision = 5)
  private BigDecimal unitPrice;

  @Column(nullable = false, precision = 5)
  private BigDecimal subtotal;
}
