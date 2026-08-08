package com.sana.cordeboheme.order_service.entity;

import com.sana.cordeboheme.common.config.BaseEntity;
import com.sana.cordeboheme.order_service.entity.enums.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "Orders")
public class Order extends BaseEntity {
  @Id @GeneratedValue private UUID orderId;

  @Column(nullable = false)
  private UUID customerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus orderStatus;

  private BigDecimal totalAmount;

  // removed for the time being
  //    @Column(nullable = false)
  //    private UUID shippingAddressId;

  // A single order can have multiple items.
  // 'mappedBy = "order"' -> OrderItem controls the foreign key in the database.
  // 'cascade = CascadeType.ALL' -> Saving/deleting an Order automatically saves/deletes its items.
  // 'orphanRemoval = true' -> Removing an item from this list deletes it from the database.
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items;
}
