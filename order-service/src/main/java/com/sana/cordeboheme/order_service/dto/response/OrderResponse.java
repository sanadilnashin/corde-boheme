package com.sana.cordeboheme.order_service.dto.response;

import com.sana.cordeboheme.order_service.entity.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
  private UUID orderId;
  private UUID customerId;
  private OrderStatus orderStatus;
  private BigDecimal totalAmount;
  private List<OrderItemResponse> items;
}
