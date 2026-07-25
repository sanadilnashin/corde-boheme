# API Gateway

## Goal

The API Gateway acts as the single entry point for all client requests in a microservices architecture.

Instead of clients calling individual services directly, every request first reaches the Gateway.

```
Client
   │
   ▼
API Gateway
   │
   ├── Product Service
   ├── Order Service
   ├── Inventory Service
   └── Payment Service
```

---

# Why do we need an API Gateway?

Without Gateway

```
Client
   ├── Product Service
   ├── Order Service
   ├── Payment Service
   └── Inventory Service
```

Problems

- Client must know every service URL.
- Security must be implemented in every service.
- Difficult to manage routing.
- No centralized logging.
- No rate limiting.

---

With Gateway

```
                Client
                   │
                   ▼
            API Gateway
                   │
     ┌─────────────┼─────────────┐
     ▼             ▼             ▼
 Product      Order        Inventory
```

Benefits

- Single entry point
- Dynamic routing
- Service discovery
- Authentication & Authorization
- Logging
- Monitoring
- Rate Limiting
- Load Balancing
- CORS handling

---

# Technologies Used

- Spring Boot 4.0.7
- Spring Cloud Gateway
- Eureka Client
- Spring Boot Actuator
- Java 21
- Maven

---

# Dependencies

added eureka client

# application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: gateway-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

management:
  endpoints:
    web:
      exposure:
        include: "*"
```

---

# Registration with Eureka

The Gateway registers itself as a Eureka Client.

```
Gateway
    │
    │ Register
    ▼
Eureka Server
```

Once registered, it becomes discoverable by other microservices.

---

# Current Architecture

```
                 Gateway (8080)
                      │
                      ▼
             Eureka Server (8761)
                      ▲
                      │
             Config Server (8888)
```

---

# Validation

Verified

- Gateway starts successfully.
- Registers with Eureka.
- Visible in Eureka Dashboard.
- Actuator endpoints enabled.

---

# Eureka Self-Preservation Warning

Observed message

```
EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP...
```

Reason

- Eureka enters Self-Preservation Mode when it receives fewer heartbeats than expected.
- This prevents accidental removal of healthy services during temporary network issues.

For local development, this warning is normal when only a few services are running.

---

# Ports Used

| Service | Port |
|----------|------|
| Discovery Server | 8761 |
| Config Server | 8888 |
| API Gateway | 8080 |

---

# Interview Notes

### What is an API Gateway?

A Gateway is the single entry point for all client requests in a microservices architecture.

### Why is it required?

- Centralized routing
- Authentication
- Authorization
- Logging
- Rate limiting
- Load balancing
- Service discovery

### Does Gateway communicate using service URLs?

No.

It communicates using service names registered in Eureka.

Example

```
lb://PRODUCT-SERVICE
```

instead of

```
http://localhost:8081
```

---

# Next Step

Create Product Service.

Flow will become

