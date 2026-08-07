package com.sana.cordeboheme.inventory_service.Exception;

import com.sana.cordeboheme.common.exception.ResourceNotFoundException;

public class InventoryNotFoundException extends ResourceNotFoundException {
  public InventoryNotFoundException(String message) {
    super(message);
  }
}
