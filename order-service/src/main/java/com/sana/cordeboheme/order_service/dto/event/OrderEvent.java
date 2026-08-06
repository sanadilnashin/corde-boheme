package com.sana.cordeboheme.order_service.dto.event;

import com.sana.cordeboheme.order_service.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderEvent {
    private UUID orderId;
    private UUID customerId;
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private List<OrderItemEvent> items;
}
