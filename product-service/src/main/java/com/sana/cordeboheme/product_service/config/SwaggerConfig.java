package com.sana.cordeboheme.product_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI cordeBohemeOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Corde Boheme Product Service API")
                .description("REST APIs for managing handmade macramé products.")
                .version("v1.0")
                .contact(new Contact().name("Sana Dilnashin").email("sanadilnashin@example.com")));
  }
}
