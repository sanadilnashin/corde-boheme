package com.sana.cordeboheme.order_service.dto.event;

import com.sana.cordeboheme.order_service.entity.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderEvent {
    private UUID orderId;
    private UUID customerId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private List<OrderItemEvent> items;
}
