package com.sana.cordeboheme.order_service.service;

import com.sana.cordeboheme.order_service.dto.request.OrderRequest;
import com.sana.cordeboheme.order_service.entity.Order;
import com.sana.cordeboheme.order_service.entity.enums.OrderStatus;

import java.util.UUID;

public interface OrderService {

    public Order createOrder(OrderRequest orderRequest);

    public  Order updateOrderStatus(UUID orderId, OrderStatus newStatus);

    public Order getOrderById(UUID orderId);

    public  Order getOrderByCustomerId(UUID customerId);


}
