package com.sana.cordeboheme.order_service.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {

  private UUID orderItemId;
  private UUID productId;
  private Integer quantity;
  private BigDecimal unitPrice;
  private BigDecimal subTotal;
}
