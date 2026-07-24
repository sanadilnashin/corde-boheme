# Discovery Server (Eureka)

## Objective

Implement a Service Discovery mechanism using Netflix Eureka.

---

## Problem Statement

In a microservices architecture, services should not communicate using hardcoded URLs.

Example:

Order Service

http://localhost:8081

Product Service

If Product Service changes its port or is deployed on another machine, every service needs to be updated.

This is not scalable.

---

## Solution

Introduce a Discovery Server.

All microservices register themselves with Eureka.

Other services query Eureka to discover service locations dynamically.

Architecture

                +----------------------+
                |   Discovery Server   |
                |      (Eureka)        |
                +----------+-----------+
                           ^
                           |
        +------------------+------------------+
        |                                     |
+---------------+                   +----------------+
| Config Server |                   | Product Service|
+---------------+                   +----------------+

---

## Technologies

- Spring Boot 4.0.7
- Spring Cloud Netflix Eureka
- Maven
- Java 21

---

## Project Creation

Generated using Spring Initializr.

Configuration

- Project : Maven
- Language : Java
- Spring Boot : 4.0.7
- Java : 21

Dependencies

- Eureka Server
- Spring Boot Actuator

---

## Parent Project Integration

Added module in parent pom.xml

```xml
<module>discovery-server</module>
```

Updated parent section in discovery-server/pom.xml

```xml
<parent>
    <groupId>com.sana.cordeboheme</groupId>
    <artifactId>corde-boheme</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

Removed duplicate dependencyManagement because it is managed by the parent project.

---

## Configuration

application.yml

```yaml
server:
  port: 8761

spring:
  application:
    name: discovery-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false

management:
  endpoints:
    web:
      exposure:
        include: "*"
```

---

## Main Class

```java
@EnableEurekaServer
@SpringBootApplication
public class DiscoveryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }

}
```

---

## Why register-with-eureka = false?

The Discovery Server is itself the registry.

It should not register with another Eureka server.

---

## Why fetch-registry = false?

The Discovery Server maintains the registry.

It does not need to download it.

---

## Verification

Application URL

http://localhost:8761

Dashboard displayed successfully.

Initially

```
No instances available
```

This is expected because no microservice has registered yet.

---

## Register Config Server

Added dependency

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

Config Server application.yml

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

After starting Discovery Server and Config Server

Dashboard displayed

```
CONFIG-SERVER
```

indicating successful registration.

---

## Key Learnings

- Purpose of Eureka
- Difference between Eureka Server and Eureka Client
- Why @EnableEurekaServer is required
- Why @EnableEurekaClient is no longer required
- Meaning of /eureka endpoint
- Difference between Dashboard and REST API