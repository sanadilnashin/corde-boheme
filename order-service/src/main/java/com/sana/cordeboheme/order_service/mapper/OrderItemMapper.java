package com.sana.cordeboheme.order_service.mapper;

import com.sana.cordeboheme.order_service.dto.request.OrderItemRequest;
import com.sana.cordeboheme.order_service.dto.response.OrderItemResponse;
import com.sana.cordeboheme.order_service.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public static OrderItem toOrderItem(OrderItemRequest orderItemRequest) {
        return OrderItem.builder()
                .productId(orderItemRequest.getProductId())
                .quantity(orderItemRequest.getQuantity())
                .build();
    }

    public static OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(orderItem.getProductId())
                .unitPrice(orderItem.getUnitPrice())
                .subTotal(orderItem.getSubtotal())
                .build();
    }
}
