package com.sana.cordeboheme.order_service.service;

import com.sana.cordeboheme.order_service.entity.Order;
import com.sana.cordeboheme.order_service.entity.OrderItem;
import java.util.UUID;

public interface OrderItemService {

  // alter order before payment

  public Order addItem(UUID orderId, OrderItem orderItem);

  public Order removeItem(UUID orderId, UUID orderItemId);
}
