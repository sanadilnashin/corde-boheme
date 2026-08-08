package com.sana.cordeboheme.order_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import lombok.Data;

@Data
public class OrderItemRequest {

  @NotNull(message = "productId can't be null")
  private UUID productId;

  @Positive
  @Min(value = 1, message = "minimum quantity should be 1")
  @NotNull(message = "quantity can't be null")
  private Integer quantity;
}
