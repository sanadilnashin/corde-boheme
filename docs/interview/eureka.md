# Eureka Interview Notes

## What is Eureka?

Netflix Eureka is a Service Discovery Server used in a microservices architecture.

It allows services to register themselves and discover other services dynamically.

---

## Why do we need Eureka?

Without Eureka

Every service communicates using hardcoded URLs.

```
Order Service
    ↓
http://localhost:8081
```

If Product Service changes location, every service must be updated.

With Eureka

```
Order Service
      ↓
Discovery Server
      ↓
Product Service
```

Services ask Eureka for locations.

---

## Eureka Server

Acts as the registry.

Required annotation

```java
@EnableEurekaServer
```

---

## Eureka Client

Registers with Eureka automatically.

Required dependency

```
spring-cloud-starter-netflix-eureka-client
```

No annotation is required in modern Spring Boot.

---

## Why /eureka ?

```
http://localhost:8761
```

Dashboard

```
http://localhost:8761/eureka/
```

REST API used by Eureka clients.

---

## register-with-eureka

Server

false

Client

true (default)

---

## fetch-registry

Server

false

Client

true (default)

---

## Frequently Asked Questions

Q. Why doesn't Eureka Server register itself?

Because it is the registry.

---

Q. Why doesn't Config Server require @EnableEurekaClient?

Modern Spring Boot automatically configures Eureka Client using auto-configuration.

---

Q. Difference between Eureka Server and Client?

Server stores the registry.

Client registers itself and discovers other services.