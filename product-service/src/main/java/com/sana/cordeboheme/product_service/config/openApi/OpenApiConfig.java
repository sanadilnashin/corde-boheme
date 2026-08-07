package com.sana.cordeboheme.product_service.config.openApi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"int", "prod"})
public class OpenApiConfig {

  @Bean
  public OpenAPI cordeBohemeOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Corde Boheme Product Service API")
                .description("REST APIs for managing handmade macramé products.")
                .version("v1.0")
                .contact(new Contact().name("Sana Dilnashin").email("sanadilnashin@example.com")))
        // Security: adds "Authorize" button in Swagger UI
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .name("bearerAuth")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        // Multiple servers: dropdown in Swagger UI
        .servers(
            List.of(
                new Server().url("https://int.cordeboheme.com").description("Integration"),
                new Server().url("https://api.cordeboheme.com").description("Production")));
  }
}
