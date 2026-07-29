package com.sana.cordeboheme.product_service.exception;

import com.sana.cordeboheme.common.exception.ResourceAlreadyExistsException;

public class ProductAlreadyExistsException extends ResourceAlreadyExistsException {
  public ProductAlreadyExistsException(String message) {
    super(message);
  }
}
