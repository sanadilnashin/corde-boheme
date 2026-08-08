package com.sana.cordeboheme.product_service.config.openApi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class OpenApiConfig_dev {
  @Bean
  public OpenAPI cordeBohemeOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Corde Boheme Product Service API")
                .description("REST APIs for managing handmade macramé products.")
                .version("v1.0")
                .contact(new Contact().name("Sana Dilnashin").email("sanadilnashin@example.com")))
        // Multiple servers: dropdown in Swagger UI
        .servers(List.of(new Server().url("http://localhost:8081").description("Local Dev")));
  }
}
