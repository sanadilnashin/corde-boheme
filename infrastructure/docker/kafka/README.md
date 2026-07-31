# Kafka Local Setup Guide

## Overview

Kafka is used for asynchronous communication between microservices.

Local setup:

- Kafka version: 3.9.1
- Mode: KRaft (No ZooKeeper)
- Broker: 1
- Port: 9092
- Replication factor: 1

Folder structure:

```
infrastructure
└── docker
    └── kafka
        ├── docker-compose.yml
        └── README.md
```

---

# Docker Compose

```yaml
services:

  kafka:
    image: apache/kafka:3.9.1

    container_name: kafka

    restart: unless-stopped

    ports:
      - "9092:9092"

    environment:

      KAFKA_NODE_ID: 1

      KAFKA_PROCESS_ROLES: broker,controller

      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9093

      KAFKA_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093

      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092

      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT

      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER

      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT

      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1

      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1

      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0

      KAFKA_HEAP_OPTS: "-Xms256m -Xmx512m"


    volumes:

      - kafka_data:/tmp/kraft-combined-logs


volumes:

  kafka_data:
```

---

# Docker Compose Tag Explanation

## image

```yaml
image: apache/kafka:3.9.1
```

Kafka Docker image.

Version is fixed to avoid unexpected upgrades.

---

## container_name

```yaml
container_name: kafka
```

Name of the Docker container.

Used in commands:

```bash
docker logs kafka
docker exec -it kafka bash
```

---

## restart

```yaml
restart: unless-stopped
```

Automatically restarts Kafka if it crashes.

---

## ports

```yaml
9092:9092
```

Maps:

```
Host machine
localhost:9092

        |
        |

Kafka container
9092
```

Spring Boot connects using:

```properties
spring.kafka.bootstrap-servers=localhost:9092
```

---

## environment

Kafka configuration.

### Node ID

```yaml
KAFKA_NODE_ID: 1
```

Unique broker identifier.

In production:

```
Broker 1 -> ID 1
Broker 2 -> ID 2
Broker 3 -> ID 3
```

---

### Process Roles

```yaml
KAFKA_PROCESS_ROLES: broker,controller
```

Single node runs:

- Kafka broker
- KRaft controller

---

### Controller Quorum

```yaml
KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9093
```

Defines Kafka controller nodes.

Format:

```
nodeId@host:port
```

---

### Listeners

```yaml
KAFKA_LISTENERS
```

Defines Kafka communication endpoints.

```
9092
|
Client communication


9093
|
Controller communication
```

---

### Advertised Listener

```yaml
KAFKA_ADVERTISED_LISTENERS
```

Address given to clients.

Example:

```
Spring Boot
     |
     |
localhost:9092
     |
     |
Kafka
```

---

### Replication Settings

```yaml
KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

Kafka internal topics replication.

Local:

```
1 broker = replication 1
```

Production:

```
3 brokers = replication 3
```

---

### Heap Memory

```yaml
KAFKA_HEAP_OPTS: "-Xms256m -Xmx512m"
```

Kafka JVM memory.

Useful for local machines with limited RAM.

---

### Volume

```yaml
kafka_data:/tmp/kraft-combined-logs
```

Stores Kafka data:

- Topics
- Messages
- Metadata

Removing volume deletes Kafka data.

---

# Start Kafka

From Kafka folder:

```bash
docker compose up -d
```

---

# Stop Kafka

```bash
docker compose down
```

---

# Stop and Remove Kafka Data

```bash
docker compose down -v
```

Useful when Kafka metadata is corrupted.

---

# Check Container Status

```bash
docker ps
```

Example:

```
CONTAINER
kafka

STATUS
Up
```

---

# View Kafka Logs

Normal logs:

```bash
docker logs kafka
```

Live logs:

```bash
docker logs -f kafka
```

---

# Enter Kafka Container

```bash
docker exec -it kafka bash
```

Meaning:

```
docker
 |
 +-- exec
      |
      +-- run command inside container
```

`-it`

- `i` = interactive
- `t` = terminal

`bash`

opens Linux shell inside container.

---

# Kafka CLI Location

Inside container:

```bash
cd /opt/kafka/bin
```

Kafka tools:

```
kafka-topics.sh

kafka-console-producer.sh

kafka-console-consumer.sh
```

---

# Topic Commands

## Create Topic

```bash
./kafka-topics.sh \
--bootstrap-server localhost:9092 \
--create \
--topic product-events \
--partitions 2 \
--replication-factor 1
```

Creates:

```
product-events

Partition-0
Partition-1
```

---

## List Topics

```bash
./kafka-topics.sh \
--bootstrap-server localhost:9092 \
--list
```

---

## Describe Topic

```bash
./kafka-topics.sh \
--bootstrap-server localhost:9092 \
--describe \
--topic product-events
```

Shows:

- partitions
- leader
- replicas

---

## Delete Topic

```bash
./kafka-topics.sh \
--bootstrap-server localhost:9092 \
--delete \
--topic product-events
```

---

# Producer Test

Send messages manually:

```bash
./kafka-console-producer.sh \
--bootstrap-server localhost:9092 \
--topic product-events
```

Type:

```json
{
 "productId":1,
 "name":"Macrame Plant Hanger"
}
```

---

# Consumer Test

Read messages:

```bash
./kafka-console-consumer.sh \
--bootstrap-server localhost:9092 \
--topic product-events \
--from-beginning
```

---

# Consumer Group Commands

List groups:

```bash
./kafka-consumer-groups.sh \
--bootstrap-server localhost:9092 \
--list
```

Describe group:

```bash
./kafka-consumer-groups.sh \
--bootstrap-server localhost:9092 \
--describe \
--group inventory-service
```

---

# Useful Docker Commands

## Show all containers

```bash
docker ps -a
```

---

## Restart Kafka

```bash
docker restart kafka
```

---

## Inspect Container

```bash
docker inspect kafka
```

---

## Check Docker Volumes

```bash
docker volume ls
```

---

## Remove Unused Resources

```bash
docker system prune
```

---

# Troubleshooting

## Kafka keeps restarting

Check:

```bash
docker logs kafka
```

---

## Topic creation timeout

Example:

```
Timed out waiting for node assignment
```

Check:

1. Kafka is running

```bash
docker ps
```

2. Kafka logs

```bash
docker logs kafka
```

3. Reset Kafka data

```bash
docker compose down -v

docker compose up -d
```

---

## Spring Boot Connection

Application config:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

Kafka is now ready for:

- Producers
- Consumers
- Event-driven microservices