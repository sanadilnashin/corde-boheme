package com.sana.cordeboheme.order_service.controller;

import com.sana.cordeboheme.order_service.dto.request.OrderRequest;
import com.sana.cordeboheme.order_service.entity.Order;
import com.sana.cordeboheme.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody @Valid OrderRequest orderRequest) {

        Order order = orderService.createOrder(orderRequest);
        return order;

    }
}
