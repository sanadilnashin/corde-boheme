package com.sana.cordeboheme.inventory_service.config;

import com.sana.cordeboheme.inventory_service.event.ProductCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
  @Bean
  ConsumerFactory<String, ProductCreatedEvent> consumerFactory() {
    Map<String, Object> config = new HashMap<>();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      config.put(
              ConsumerConfig.GROUP_ID_CONFIG,
              "inventory-service");

      config.put(
              ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
              "earliest");
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
      return new DefaultKafkaConsumerFactory<>(
              config,
              new StringDeserializer(),
              new JacksonJsonDeserializer<>(ProductCreatedEvent.class));  }

}
