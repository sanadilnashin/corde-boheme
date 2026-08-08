package com.sana.cordeboheme.order_service.service.impl;

import com.sana.cordeboheme.order_service.client.FeignClient;
import com.sana.cordeboheme.order_service.client.dto.ProductResponse;
import com.sana.cordeboheme.order_service.dto.request.OrderRequest;
import com.sana.cordeboheme.order_service.dto.response.OrderResponse;
import com.sana.cordeboheme.order_service.entity.Order;
import com.sana.cordeboheme.order_service.entity.OrderItem;
import com.sana.cordeboheme.order_service.entity.enums.OrderStatus;
import com.sana.cordeboheme.order_service.exception.OrderNotFountException;
import com.sana.cordeboheme.order_service.mapper.OrderMapper;
import com.sana.cordeboheme.order_service.repository.OrderRepository;
import com.sana.cordeboheme.order_service.service.OrderService;
import com.sana.cordeboheme.order_service.util.PriceCalculator;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  private OrderRepository orderRepository;
  private FeignClient feignClient;

  public OrderServiceImpl(FeignClient feignClient, OrderRepository orderRepository) {
    this.feignClient = feignClient;
    this.orderRepository = orderRepository;
  }

  @Override
  public OrderResponse createOrder(OrderRequest orderRequest) {

    Order order = OrderMapper.toOrder(orderRequest);

    List<OrderItem> orderItems =
        order.getItems().stream()
            .map(
                item -> {
                  ProductResponse product =
                      feignClient.getProductById(
                          item.getProductId().getMostSignificantBits() & Long.MAX_VALUE);
                  item.setSubtotal(
                      PriceCalculator.calculateSubTotal(item.getQuantity(), product.price()));
                  return item;
                })
            .toList();

    BigDecimal totalAmount = PriceCalculator.calculateTotalAmount(orderItems);
    order.setOrderStatus(OrderStatus.PENDING);
    order.setTotalAmount(totalAmount);
    order.setItems(orderItems);

    Order savedOrder = orderRepository.save(order);
    return OrderMapper.toOrderResponse(savedOrder);
  }

  @Override
  public Boolean updateOrderStatus(UUID orderId, OrderStatus newStatus) {

    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new OrderNotFountException("Order not found with id" + orderId));
    order.setOrderStatus(newStatus);
    orderRepository.save(order);
    return true;
  }

  @Override
  public OrderResponse getOrderById(UUID orderId) {
    return orderRepository
        .findById(orderId)
        .map(OrderMapper::toOrderResponse)
        .orElseThrow(() -> new OrderNotFountException("Order not foun with id" + orderId));
  }

  @Override
  public List<OrderResponse> getOrderByCustomerId(UUID customerId) {
    List<Order> orders = orderRepository.findByCustomerId(customerId);
    if (orders == null) {
      throw new OrderNotFountException("order not found by customer id" + customerId);
    }
    return orders.stream().map(OrderMapper::toOrderResponse).toList();
    // return OrderMapper.toOrderResponse(order);
  }

  @Override
  public void deleteOrderByOrderId(UUID orderId) {
    orderRepository
        .findById(orderId)
        .orElseThrow(
            () -> new OrderNotFountException("order not founf with id to delete" + orderId));
    orderRepository.deleteById(orderId);
  }
}
