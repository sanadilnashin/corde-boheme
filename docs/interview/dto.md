# DTO (Data Transfer Object)

DTO is used to transfer data between client and server.

Why use DTO?

- Hide internal entity fields
- Prevent exposing database model
- Validate incoming requests
- Return only required data
- Decouple API from database schema

Request DTO → Client to Server

Response DTO → Server to Client

In Java 21, DTOs are commonly implemented using records because they are immutable and require very little boilerplate.