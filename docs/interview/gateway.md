# API Gateway Interview Notes

## What is an API Gateway?

Single entry point for all client requests in a microservices architecture.

---

## Why use an API Gateway?

- Routing
- Authentication
- Authorization
- Load Balancing
- Rate Limiting
- Logging
- Monitoring
- CORS
- SSL Termination

---

## Why not let clients call services directly?

Problems

- Multiple URLs
- Tight coupling
- Security duplication
- Difficult versioning

---

## How does Gateway locate Product Service?

Using Eureka.

Example

```
lb://PRODUCT-SERVICE
```

instead of

```
http://localhost:8081
```

---

## Difference

| Gateway | Eureka |
|----------|---------|
| Routes requests | Stores service registry |
| Entry point | Discovery mechanism |
| Receives client traffic | Does not receive client traffic |

---

## Common Interview Questions

### What happens if Gateway is down?

Clients cannot access backend services.

---

### Can services communicate without Gateway?

Yes.

Using Eureka + Feign/WebClient.

---

### Does Gateway store business logic?

No.

Only cross-cutting concerns.

---

### Why is Gateway stateless?

To allow multiple instances behind a load balancer.

---

### What protocols does Spring Cloud Gateway support?

- HTTP
- HTTPS
- WebSocket