package com.sana.cordeboheme.order_service.exception;

import com.sana.cordeboheme.common.exception.ResourceNotFoundException;

public class OrderNotFountException extends ResourceNotFoundException {
  public OrderNotFountException(String message) {
    super(message);
  }
}
