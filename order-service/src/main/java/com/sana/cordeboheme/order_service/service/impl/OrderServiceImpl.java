package com.sana.cordeboheme.order_service.service.impl;

import com.sana.cordeboheme.order_service.dto.request.OrderRequest;
import com.sana.cordeboheme.order_service.entity.Order;
import com.sana.cordeboheme.order_service.entity.enums.OrderStatus;
import com.sana.cordeboheme.order_service.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderServiceImpl  implements OrderService {

    @Override
    public Order createOrder(OrderRequest orderRequest) {
        return null;
    }

    @Override
    public Order updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        return null;
    }

    @Override
    public Order getOrderById(UUID orderId) {
        return null;
    }

    @Override
    public Order getOrderByCustomerId(UUID customerId) {
        return null;
    }
}
