package com.sana.cordeboheme.order_service.util;

import com.sana.cordeboheme.order_service.entity.OrderItem;
import java.math.BigDecimal;
import java.util.List;

public class PriceCalculator {
  public static BigDecimal calculateSubTotal(Integer qty, BigDecimal unitPrice) {
    return unitPrice.multiply(BigDecimal.valueOf(qty));
  }

  public static BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
    return orderItems.stream().map(e -> e.getSubtotal()).reduce(BigDecimal::add).get();
  }
}
