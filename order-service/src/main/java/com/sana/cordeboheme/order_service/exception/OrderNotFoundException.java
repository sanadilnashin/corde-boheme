package com.sana.cordeboheme.order_service.exception;

import com.sana.cordeboheme.common.exception.ResourceNotFoundException;

public class OrderNotFoundException extends ResourceNotFoundException {
  public OrderNotFoundException(String message) {
    super(message);
  }
}
