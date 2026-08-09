package com.sana.cordeboheme.order_service.mapper;

import com.sana.cordeboheme.order_service.dto.request.OrderRequest;
import com.sana.cordeboheme.order_service.dto.response.OrderItemResponse;
import com.sana.cordeboheme.order_service.dto.response.OrderResponse;
import com.sana.cordeboheme.order_service.entity.Order;
import com.sana.cordeboheme.order_service.entity.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

  public static Order toOrder(OrderRequest orderRequest) {
    Order order = Order.builder().customerId(orderRequest.getCustomerId()).build();
    List<OrderItem> orderItems =
        orderRequest.getItems().stream()
            .map(OrderItemMapper::toOrderItem)
            .peek(orderItem -> orderItem.setOrder(order))
            .toList();
    order.setItems(orderItems);
    return order;
  }

  public static OrderResponse toOrderResponse(Order order) {
    OrderResponse orderResponse =
        com.sana.cordeboheme.order_service.dto.response.OrderResponse.builder()
            .orderId(order.getOrderId())
            .customerId(order.getCustomerId())
            .orderStatus(order.getOrderStatus())
            .totalAmount(order.getTotalAmount())
            .build();

    List<OrderItemResponse> orderItemResponses =
        order.getItems().stream().map(OrderItemMapper::toOrderItemResponse).toList();
    orderResponse.setItems(orderItemResponses);

    return orderResponse;
  }
}
