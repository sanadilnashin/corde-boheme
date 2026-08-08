package com.sana.cordeboheme.order_service.service;

import com.sana.cordeboheme.order_service.dto.request.OrderRequest;
import com.sana.cordeboheme.order_service.dto.response.OrderResponse;
import com.sana.cordeboheme.order_service.entity.enums.OrderStatus;
import java.util.List;
import java.util.UUID;

public interface OrderService {

  OrderResponse createOrder(OrderRequest orderRequest);

  Boolean updateOrderStatus(UUID orderId, OrderStatus newStatus);

  OrderResponse getOrderById(UUID orderId);

  List<OrderResponse> getOrderByCustomerId(UUID customerId);

  void deleteOrderByOrderId(UUID orderId);
}
