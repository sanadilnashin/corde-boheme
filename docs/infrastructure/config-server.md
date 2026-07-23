Include

Why Config Server?
Spring Cloud Config Server is a centralized configuration
management service for microservices. 
Instead of each service maintaining its own configuration,
all configuration is stored in a single location—either 
a Git repository or a native file system.
On startup, each microservice fetches its configuration from the Config Server.
This improves maintainability, supports multiple environments, enables version-controlled configuration, and separates configuration from application code.
Config Server
│
┌───────────────────┼───────────────────┐
│                   │                   │
Product Service    Order Service     Inventory Service
Architecture
Native Profile
Spring Cloud Config
application.yml
Verification
Troubleshooting