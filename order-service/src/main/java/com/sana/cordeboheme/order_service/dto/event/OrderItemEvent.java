package com.sana.cordeboheme.order_service.dto.event;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemEvent {

    private UUID productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;

}
