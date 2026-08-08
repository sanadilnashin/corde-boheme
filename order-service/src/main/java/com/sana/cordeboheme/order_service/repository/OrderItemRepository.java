package com.sana.cordeboheme.order_service.repository;

import com.sana.cordeboheme.order_service.dto.request.OrderItemRequest;
import com.sana.cordeboheme.order_service.entity.OrderItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

  OrderItem save(OrderItemRequest orderItemRequest);
}
