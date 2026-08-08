package com.sana.cordeboheme.inventory_service.Exception;

import com.sana.cordeboheme.common.exception.ResourceAlreadyExistsException;

public class InventoryAlreadyExistsException extends ResourceAlreadyExistsException {
  public InventoryAlreadyExistsException(String message) {
    super(message);
  }
}
