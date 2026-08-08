package com.sana.cordeboheme.order_service.service.impl;

import com.sana.cordeboheme.order_service.entity.Order;
import com.sana.cordeboheme.order_service.entity.OrderItem;
import com.sana.cordeboheme.order_service.service.OrderItemService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderItemImpl implements OrderItemService {
  @Override
  public Order addItem(UUID orderId, OrderItem orderItem) {
    return null;
  }

  @Override
  public Order removeItem(UUID orderId, UUID orderItemId) {
    return null;
  }
}
