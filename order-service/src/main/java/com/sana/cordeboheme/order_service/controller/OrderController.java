package com.sana.cordeboheme.order_service.controller;

import com.sana.cordeboheme.order_service.dto.request.OrderRequest;
import com.sana.cordeboheme.order_service.dto.response.OrderResponse;
import com.sana.cordeboheme.order_service.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public OrderResponse createOrder(@RequestBody @Valid OrderRequest orderRequest) {

    OrderResponse order = orderService.createOrder(orderRequest);
    return order;
  }

  @GetMapping
  public OrderResponse getOrderById(@RequestParam UUID id) {
    return orderService.getOrderById(id);
  }

  @GetMapping("/customer")
  public List<OrderResponse> getOrderByCustomerId(@RequestParam UUID id) {
    return orderService.getOrderByCustomerId(id);
  }
}
