package com.sana.cordeboheme.order_service.dto.response;

import com.sana.cordeboheme.order_service.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID orderId;
    private UUID customerId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;

}
