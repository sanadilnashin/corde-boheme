package com.sana.cordeboheme.inventory_service.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductEventPublisher {
  // ehen ever product is created published an event to topic
  private static final String PRODUCT_CREATED_TOPIC = "product-created";
  // key,msg/event
  private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

  public ProductEventPublisher(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  // public event
  public void publishProductCreated(UUID productId, String sku) {
    // create event
    ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId, sku);
    kafkaTemplate.send(PRODUCT_CREATED_TOPIC, productId.toString(), productCreatedEvent);
  }
}
