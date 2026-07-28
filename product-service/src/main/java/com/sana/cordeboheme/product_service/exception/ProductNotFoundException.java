package com.sana.cordeboheme.product_service.exception;

import com.sana.cordeboheme.common.exception.ResourceNotFoundException;

public class ProductNotFoundException extends ResourceNotFoundException {
  public ProductNotFoundException(String message) {
    super(message);
  }
}
