# Order Service Development Checklist

## Phase 1: Project Setup

- [X] Configure Spring Data JPA
- [ X] Configure Database
- [ ] Configure Kafka Producer
- [ ] Configure JSON Serializer
- [ ] Verify Database Connection
- [ ] Verify Kafka Connection

---

# Phase 2: Domain Model

## Entities

- [x] Order
- [x] OrderItem
- [x] OrderStatus Enum

## Repositories

- [X ] OrderRepository

---

# Phase 3: DTO Design

## CreateOrderRequest

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public class CreateOrderRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Shipping address ID is required")
    private UUID shippingAddressId;

    @Valid
    @NotNull(message = "Order items cannot be null")
    @Size(min = 1, message = "Order must contain at least one item")
    private List<CreateOrderItemRequest> items;

    // Getters and Setters
}
```

### Best Practices

- **Use `@Valid`** on nested objects to trigger validation on `CreateOrderItemRequest`
- **`@NotNull`** ensures UUID fields are present (UUIDs can't be blank, only null)
- **`@Size(min = 1)`** validates the list is not empty
- Consider adding custom validation for cross-field validation if needed

### Tasks

- [x] Create CreateOrderRequest
- [x] Add Bean Validation
- [x] Validate items list is not empty

---

## CreateOrderItemRequest

```java
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreateOrderItemRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // Getters and Setters
}
```

### Best Practices

- **`@Min(1)`** prevents zero or negative quantities
- Consider adding **`@Max`** if there's a business limit (e.g., max 999 items)
- Use **`@DecimalMin`** if switching to `BigDecimal` for fractional quantities

### Tasks

- [x] Create CreateOrderItemRequest
- [x] Add quantity validation

---

## OrderResponse

```java
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderResponse {

    private UUID orderId;

    private UUID customerId;

    private OrderStatus orderStatus;

    private BigDecimal totalAmount;

    private UUID shippingAddressId;

    private List<OrderItemResponse> items;

    // Consider adding:
    // - LocalDateTime createdAt
    // - LocalDateTime updatedAt
    // - String trackingNumber (if applicable)

    // Getters and Setters
}
```

### Best Practices

- Use **BigDecimal** for monetary values (already implemented ✓)
- Consider adding **temporal fields** (createdAt, updatedAt) for audit trail
- Keep response DTOs flat and focused on client needs

### Tasks

- [x] Create OrderResponse

---

## OrderItemResponse

```java
import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemResponse {

    private UUID orderItemId;

    private UUID productId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    // Consider adding:
    // - String productName
    // - String productSku
    // - String productImageUrl

    // Getters and Setters
}
```

### Best Practices

- **subtotal = unitPrice × quantity** - ensure this is calculated, not stored
- Consider enriching with product details from Product Service (name, SKU)
- Keep minimal - client can compute subtotal if needed

### Tasks

- [x] Create OrderItemResponse

---

# Phase 4: Kafka Event Design

## Topic

```
order-created
```

---

## OrderCreatedEvent

```java
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderCreatedEvent {

    private UUID orderId;

    private UUID customerId;

    private UUID shippingAddressId;

    private OrderStatus orderStatus;

    private BigDecimal totalAmount;

    private List<OrderItemEvent> items;

    // Getters and Setters
}
```

### Best Practices

- **Event schema should be immutable** - consider making fields `final`
- Add **schema version** field for backward compatibility
- Use **record** class (Java 16+) for true immutability:
  ```java
  public record OrderCreatedEvent(
      UUID orderId,
      UUID customerId,
      // ...
  ) {}
  ```
- Include **correlationId** for distributed tracing across services

### Tasks

- [x] Create OrderCreatedEvent

---

## OrderItemEvent

```java
import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemEvent {

    private UUID productId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    // Getters and Setters
}
```

### Best Practices

- Keep events **minimal and focused** - include only data consumers need
- Avoid nested objects unless absolutely necessary
- Use **records** for immutability (Java 16+):
  ```java
  public record OrderItemEvent(
      UUID productId,
      Integer quantity,
      BigDecimal unitPrice,
      BigDecimal subtotal
  ) {}
  ```

### Tasks

- [x] Create OrderItemEvent

---

# Phase 5: Mapper

## Recommended Approach: MapStruct

Use **MapStruct** for type-safe, compile-time generated mappers:

```java
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    
    // Request → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", source = "items")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(CreateOrderRequest request);
    
    // Entity → Response
    OrderResponse toResponse(Order order);
    
    // Entity → Event
    OrderCreatedEvent toEvent(Order order);
    
    // Item mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(CreateOrderItemRequest request);
    
    OrderItemResponse toItemResponse(OrderItem item);
    
    OrderItemEvent toItemEvent(OrderItem item);
}
```

### Alternative: Manual Mapping with Builder Pattern

If avoiding MapStruct, use builder pattern:

```java
Order order = Order.builder()
    .customerId(request.getCustomerId())
    .shippingAddressId(request.getShippingAddressId())
    .status(OrderStatus.PENDING)
    .orderItems(request.getItems().stream()
        .map(this::toOrderItem)
        .collect(Collectors.toList()))
    .build();
```

### Best Practices

- **MapStruct** reduces boilerplate and is type-safe
- **Builder pattern** improves readability for complex objects
- Always **ignore auto-generated fields** (id, timestamps) in mappings
- Test mappers separately with unit tests

### Tasks

- [ ] Add MapStruct dependency to pom.xml
- [ ] Create OrderMapper interface
- [ ] Request → Entity mapping
- [ ] Entity → Response mapping
- [ ] Entity → OrderCreatedEvent mapping

---

# Phase 6: Business Logic

## createOrder()

### Validation Strategy

- [ ] **DTO Validation** - Use Jakarta Validation annotations on request DTOs (automatic with `@Valid`)
- [ ] **Service Validation** - Business rule validation (customer exists, address valid, products available)
- [ ] **Database Constraints** - Add unique constraints, foreign keys at DB level

### Validation Best Practices

```java
// 1. Controller level - automatic validation
@PostMapping
public ResponseEntity<OrderResponse> createOrder(
    @Valid @RequestBody CreateOrderRequest request) {
    
    OrderResponse response = orderService.createOrder(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

// 2. Service level - business validation
@Service
@Validated
public class OrderServiceImpl implements OrderService {
    
    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        // Validate customer exists (call Customer Service or check DB)
        validateCustomerExists(request.getCustomerId());
        
        // Validate shipping address exists
        validateShippingAddress(request.getShippingAddressId());
        
        // Validate all products exist and are active
        validateProductsExist(request.getItems());
        
        // ... rest of logic
    }
}
```

### Validation Tasks

- [ ] DTO validation (automatic with `@Valid`)
- [ ] Validate customerId exists (Customer Service or DB)
- [ ] Validate shippingAddressId exists (Customer Service or DB)
- [ ] Validate items are present (DTO level)
- [ ] Validate quantity > 0 (DTO level)

---

### Product Validation

**Use Feign Client for synchronous REST calls**:

```java
// Feign Client
@FeignClient(name = "product-service", configuration = ProductServiceConfig.class)
public interface ProductServiceClient {
    
    @GetMapping("/api/products/{productId}")
    ProductResponse getProductById(@PathVariable UUID productId);
    
    // Batch validation for better performance
    @PostMapping("/api/products/batch")
    List<ProductResponse> getProductsByIds(@RequestBody List<UUID> productIds);
}

// Service usage with batch validation
public void validateProductsExist(List<CreateOrderItemRequest> items) {
    List<UUID> productIds = items.stream()
        .map(CreateOrderItemRequest::getProductId)
        .collect(Collectors.toList());
    
    List<ProductResponse> products = productServiceClient.getProductsByIds(productIds);
    
    // Check all products exist
    if (products.size() != items.size()) {
        throw new ProductNotFoundException("Some products not found");
    }
    
    // Check products are active
    products.forEach(product -> {
        if (!product.getActive()) {
            throw new ProductInactiveException(product.getId());
        }
    });
}
```

### Tasks

- [ ] Add Feign Client dependency (Spring Cloud OpenFeign)
- [ ] Create ProductServiceClient interface
- [ ] Call Product Service to validate products
- [ ] Verify products exist and are active
- [ ] Fetch product prices
- [ ] **Optimization**: Batch validation (single call for all items)

---

### Price Calculation

```java
@Component
public class PriceCalculator {
    
    private static final MathContext ROUNDING_CONTEXT = 
        new MathContext(2, RoundingMode.HALF_EVEN);
    
    public BigDecimal calculateSubtotal(BigDecimal unitPrice, Integer quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity), ROUNDING_CONTEXT);
    }
    
    public BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add, ROUNDING_CONTEXT);
    }
    
    public BigDecimal calculateTax(BigDecimal amount, BigDecimal taxRate) {
        return amount.multiply(taxRate, ROUNDING_CONTEXT);
    }
    
    public BigDecimal calculateDiscount(BigDecimal amount, BigDecimal discountPercentage) {
        return amount.multiply(discountPercentage, ROUNDING_CONTEXT);
    }
}
```

### Best Practices

- Use **BigDecimal** for all monetary calculations (never `double` or `float`)
- Define **rounding strategy** explicitly (`HALF_EVEN` is standard for finance)
- Use **MathContext** for consistent precision
- Consider **currency validation** - ensure all prices in same currency
- Add **tax calculation** hooks for future extensibility

### Tasks

- [ ] Create PriceCalculator component
- [ ] Calculate subtotal (unitPrice × quantity)
- [ ] Calculate totalAmount (sum of all subtotals)
- [ ] Define rounding strategy
- [ ] Add tax calculation method (future use)

---

### Order Creation

```java
@Override
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    // 1. Validate
    validateRequest(request);
    
    // 2. Fetch and validate products
    Map<UUID, ProductResponse> products = fetchAndValidateProducts(request.getItems());
    
    // 3. Build order items with prices
    List<OrderItem> orderItems = request.getItems().stream()
        .map(item -> {
            BigDecimal unitPrice = products.get(item.getProductId()).getPrice();
            BigDecimal subtotal = priceCalculator.calculateSubtotal(unitPrice, item.getQuantity());
            
            return OrderItem.builder()
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .unitPrice(unitPrice)
                .subtotal(subtotal)
                .build();
        })
        .collect(Collectors.toList());
    
    // 4. Calculate total
    BigDecimal totalAmount = priceCalculator.calculateTotalAmount(orderItems);
    
    // 5. Create order
    Order order = Order.builder()
        .customerId(request.getCustomerId())
        .shippingAddressId(request.getShippingAddressId())
        .status(OrderStatus.PENDING)
        .totalAmount(totalAmount)
        .orderItems(orderItems)
        .build();
    
    // 6. Save (cascade persists items)
    Order savedOrder = orderRepository.save(order);
    
    // 7. Publish event (async, after commit)
    publishOrderCreatedEvent(savedOrder);
    
    // 8. Return response
    return orderMapper.toResponse(savedOrder);
}
```

### Tasks

- [ ] Create Order entity with Builder pattern
- [ ] Set customerId
- [ ] Set shippingAddressId
- [ ] Set status = PENDING
- [ ] Set totalAmount
- [ ] Link OrderItems to Order (bidirectional)

---

### Order Items

**Bidirectional relationship management**:

```java
// In Order entity
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> orderItems = new ArrayList<>();

// Helper method to maintain both sides
public void addOrderItem(OrderItem item) {
    orderItems.add(item);
    item.setOrder(this);
}

public void removeOrderItem(OrderItem item) {
    orderItems.remove(item);
    item.setOrder(null);
}

// In service
Order order = Order.builder()...build();

request.getItems().forEach(itemRequest -> {
    OrderItem orderItem = OrderItem.builder()
        .productId(itemRequest.getProductId())
        .quantity(itemRequest.getQuantity())
        .unitPrice(unitPrice)
        .subtotal(subtotal)
        .build();
    
    order.addOrderItem(orderItem); // Maintains both sides
});
```

### Best Practices

- **Always maintain both sides** of bidirectional relationships
- Use **helper methods** (`addOrderItem`, `removeOrderItem`) to ensure consistency
- **cascade = CascadeType.ALL** ensures children are saved with parent
- **orphanRemoval = true** deletes items removed from order
- Initialize collections to **empty ArrayList** to avoid NullPointerException

### Tasks

- [ ] Create OrderItem entities
- [ ] Set Order reference (bidirectional)
- [ ] Set productId
- [ ] Set quantity
- [ ] Set unitPrice
- [ ] Set subtotal

---

### Persistence

```java
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        // ... validation and order building ... 
        
        Order savedOrder = orderRepository.save(order);
        
        // Event will be published after transaction commits
        // See Phase 7 for implementation
        
        return orderMapper.toResponse(savedOrder);
    }
}
```

### Database Schema Considerations

```sql
-- Indexes for performance
CREATE INDEX idx_order_customer_id ON orders(customer_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_created_at ON orders(created_at);

-- Foreign key constraints
ALTER TABLE order_items 
    ADD CONSTRAINT fk_order_items_order 
    FOREIGN KEY (order_id) REFERENCES orders(id) 
    ON DELETE CASCADE;
```

### Best Practices

- Use **`@Transactional`** on service methods
- **Cascade persist** OrderItems from Order
- Add **database indexes** on frequently queried columns
- Consider **optimistic locking** with `@Version` for concurrent updates
- Return **Response DTO**, not Entity

### Tasks

- [ ] Save Order (with cascade)
- [ ] Verify Cascade persists OrderItems
- [ ] Add database indexes
- [ ] Add foreign key constraints

---

# Phase 7: Kafka Producer

## OrderEventProducer Implementation

```java
@Service
public class OrderEventProducer {
    
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    // Topic name from configuration
    private static final String ORDER_CREATED_TOPIC = "order-created";
    
    public OrderEventProducer(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        // Use orderId as key for partitioning and ordering
        String key = event.getOrderId().toString();
        
        // Send with callback for logging
        kafkaTemplate.send(ORDER_CREATED_TOPIC, key, event)
            .addCallback(
                success -> {
                    if (success != null && success.getRecordMetadata() != null) {
                        log.info("Event sent successfully: topic={}, partition={}, offset={}",
                            success.getRecordMetadata().topic(),
                            success.getRecordMetadata().partition(),
                            success.getRecordMetadata().offset());
                    }
                },
                failure -> {
                    log.error("Failed to send OrderCreatedEvent for orderId={}", 
                        event.getOrderId(), failure);
                    // Consider sending to DLQ or implementing retry logic
                    handleSendFailure(event, failure);
                }
            );
    }
    
    private void handleSendFailure(OrderCreatedEvent event, Throwable failure) {
        // Option 1: Throw exception (transaction will rollback)
        // throw new KafkaPublishException("Failed to publish event", failure);
        
        // Option 2: Save to outbox for retry
        // saveToOutbox(event);
        
        // Option 3: Log and continue (at-least-once delivery)
        log.error("Event publish failed, manual retry may be needed", failure);
    }
}
```

### Kafka Configuration (application.yml)

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all  # Wait for all replicas
      retries: 3
      retry-backoff-ms: 1000
      properties:
        # Type mapping for JsonSerializer
        spring.json.type.mapping: >
          orderCreated:com.sana.cordeboheme.dto.OrderCreatedEvent,
          orderItem:com.sana.cordeboheme.dto.OrderItemEvent
        spring.json.add.type.headers: false
    template:
      default-topic: order-created
```

### Event Publishing After Transaction Commit

**Critical: Ensure event is published only after database commit**:

```java
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    
    private final OrderEventProducer eventProducer;
    
    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        // ... validation and order creation ...
        
        Order savedOrder = orderRepository.save(order);
        
        // Register synchronization to publish AFTER transaction commits
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    // Publish event only if transaction succeeded
                    OrderCreatedEvent event = orderMapper.toEvent(savedOrder);
                    eventProducer.sendOrderCreatedEvent(event);
                }
                
                @Override
                public void afterCompletion(int status) {
                    if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                        log.warn("Transaction rolled back, event not published for orderId={}", 
                            savedOrder.getId());
                    }
                }
            }
        );
        
        return orderMapper.toResponse(savedOrder);
    }
}
```

### Alternative: Transactional Outbox Pattern

For guaranteed delivery, use transactional outbox:

```java
@Entity
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String aggregateType;  // "Order"
    private String aggregateId;    // orderId
    private String eventType;      // "OrderCreatedEvent"
    @Lob
    private String payload;        // JSON payload
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}

@Service
public class OutboxService {
    
    @Transactional
    public void saveOrderWithOutbox(Order order) {
        orderRepository.save(order);
        
        OrderCreatedEvent event = orderMapper.toEvent(order);
        outboxRepository.save(OutboxEvent.from(event));
    }
}
```

### Best Practices

- **Always publish after transaction commits** using `TransactionSynchronizationManager`
- **Use message keys** for partitioning (ensures ordering per order)
- **Configure acks=all** for durability
- **Add callbacks** for success/failure handling
- **Consider transactional outbox** for guaranteed delivery
- **Use correlation IDs** for distributed tracing

### Tasks

- [ ] Create OrderEventProducer service
- [ ] Configure KafkaTemplate with String key
- [ ] Add success/failure callbacks
- [ ] Implement publish-after-commit pattern
- [ ] Add correlation ID to events

---

# Phase 8: REST API

## Endpoint

```http
POST /api/orders
Content-Type: application/json
Authorization: Bearer {jwt-token}

Request Body:
{
  "customerId": "uuid",
  "shippingAddressId": "uuid",
  "items": [
    {
      "productId": "uuid",
      "quantity": 2
    }
  ]
}

Response (201 Created):
{
  "orderId": "uuid",
  "customerId": "uuid",
  "orderStatus": "PENDING",
  "totalAmount": 99.99,
  "shippingAddressId": "uuid",
  "items": [...]
}
```

### Controller Implementation

```java
@RestController
@RequestMapping("/api/orders")
@Validated
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * Creates a new order
     * @param request Order creation request
     * @return Created order with 201 status
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        
        log.info("Received create order request: customerId={}, itemsCount={}", 
            request.getCustomerId(), 
            request.getItems().size());
        
        OrderResponse response = orderService.createOrder(request);
        
        // Build Location header URI
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{orderId}")
            .buildAndExpand(response.getOrderId())
            .toUri();
        
        return ResponseEntity
            .created(location)  // HTTP 201 with Location header
            .body(response);
    }
    
    // Future endpoints
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        // Implementation in future
        return null;
    }
}
```

### Controller Best Practices

- **Return `ResponseEntity`** to control status codes and headers
- **HTTP 201 Created** with **Location header** pointing to new resource
- Use **`@Valid`** for automatic DTO validation
- Add **`@Validated`** at class level for path variable validation
- Use **`@Slf4j`** for logging requests
- Consider **OpenAPI annotations** for API documentation:
  ```java
  @Operation(summary = "Create a new order", description = "Creates a new order with items")
  @ApiResponse(responseCode = "201", description = "Order created successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request")
  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(...)
  ```

### Global Controller Configuration

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PATCH")
            .maxAge(3600);
    }
}
```

### Tasks

- [ ] Create OrderController with `@RestController`
- [ ] Add POST endpoint with `@PostMapping`
- [ ] Validate request with `@Valid`
- [ ] Return HTTP 201 with Location header
- [ ] Add request/response logging
- [ ] Add OpenAPI annotations

---

# Phase 9: Exception Handling

## Custom Exception Hierarchy

```java
// Base exception for all order-related errors
public abstract class OrderException extends RuntimeException {
    private final String code;
    
    protected OrderException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    protected OrderException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}

// Specific exceptions
public class ProductNotFoundException extends OrderException {
    public ProductNotFoundException(UUID productId) {
        super("PRODUCT_NOT_FOUND", 
              "Product not found: " + productId);
    }
}

public class ProductInactiveException extends OrderException {
    public ProductInactiveException(UUID productId) {
        super("PRODUCT_INACTIVE", 
              "Product is not available for purchase: " + productId);
    }
}

public class InvalidQuantityException extends OrderException {
    public InvalidQuantityException(Integer quantity) {
        super("INVALID_QUANTITY", 
              "Invalid quantity: " + quantity + ". Must be >= 1");
    }
}

public class EmptyOrderException extends OrderException {
    public EmptyOrderException() {
        super("EMPTY_ORDER", 
              "Order must contain at least one item");
    }
}

public class InvalidShippingAddressException extends OrderException {
    public InvalidShippingAddressException(UUID addressId) {
        super("INVALID_SHIPPING_ADDRESS", 
              "Shipping address not found: " + addressId);
    }
}

public class CustomerNotFoundException extends OrderException {
    public CustomerNotFoundException(UUID customerId) {
        super("CUSTOMER_NOT_FOUND", 
              "Customer not found: " + customerId);
    }
}

public class KafkaPublishException extends OrderException {
    public KafkaPublishException(String message, Throwable cause) {
        super("KAFKA_PUBLISH_ERROR", message, cause);
    }
}
```

## Global Exception Handler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handle validation errors from @Valid annotation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
        
        log.warn("Validation failed: {}", errors);
        
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("VALIDATION_ERROR", errors));
    }
    
    /**
     * Handle custom order exceptions
     */
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorResponse> handleOrderException(OrderException ex) {
        log.error("Order error: {}", ex.getCode(), ex);
        
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(ex.getCode(), List.of(ex.getMessage())));
    }
    
    /**
     * Handle Feign client exceptions (Product Service unavailable)
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        log.error("External service error", ex);
        
        String code = "EXTERNAL_SERVICE_ERROR";
        String message = "Service temporarily unavailable";
        
        if (ex instanceof FeignException.NotFound) {
            code = "PRODUCT_NOT_FOUND";
            message = "Product service returned 404";
        } else if (ex instanceof FeignException.ServiceUnavailable) {
            code = "SERVICE_UNAVAILABLE";
            message = "Product service is unavailable";
        }
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorResponse(code, List.of(message)));
    }
    
    /**
     * Handle Kafka exceptions
     */
    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ErrorResponse> handleKafkaException(KafkaException ex) {
        log.error("Kafka error", ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("KAFKA_ERROR", 
                List.of("Failed to publish event. Order created but event pending.")));
    }
    
    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_ERROR", 
                List.of("An unexpected error occurred. Please try again.")));
    }
}
```

## Error Response DTO

```java
public record ErrorResponse(
    String code,
    List<String> messages,
    LocalDateTime timestamp
) {
    
    public ErrorResponse(String code, List<String> messages) {
        this(code, messages, LocalDateTime.now());
    }
    
    // Factory methods for common errors
    public static ErrorResponse validationError(List<String> errors) {
        return new ErrorResponse("VALIDATION_ERROR", errors);
    }
    
    public static ErrorResponse notFound(String resource, UUID id) {
        return new ErrorResponse("NOT_FOUND", 
            List.of(resource + " not found: " + id));
    }
}
```

## Exception Handling Best Practices

- **Use specific exceptions** instead of generic `RuntimeException`
- **Include error codes** for client-side handling
- **Log exceptions** with appropriate levels (WARN for validation, ERROR for system errors)
- **Don't expose sensitive information** in error messages
- **Return consistent error format** across all endpoints
- **Use `@RestControllerAdvice`** for global exception handling
- **Map external exceptions** (Feign, Kafka) to user-friendly messages

## Exception Handling Tasks

- [ ] Create base `OrderException` class
- [ ] Create specific exceptions (ProductNotFound, InvalidQuantity, etc.)
- [ ] Create `GlobalExceptionHandler` with `@RestControllerAdvice`
- [ ] Handle validation errors (`MethodArgumentNotValidException`)
- [ ] Handle Feign exceptions (external service errors)
- [ ] Handle Kafka exceptions
- [ ] Create `ErrorResponse` DTO with code and messages
- [ ] Add logging to all exception handlers

---

# Phase 10: Testing

## Unit Tests

### Mapper Tests

```java
@ExtendWith(MockitoExtension.class)
class OrderMapperTest {
    
    private OrderMapper mapper = OrderMapper.INSTANCE;
    
    @Test
    void shouldMapCreateOrderRequestToOrderEntity() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setShippingAddressId(UUID.randomUUID());
        
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setProductId(UUID.randomUUID());
        itemRequest.setQuantity(2);
        request.setItems(List.of(itemRequest));
        
        // When
        Order order = mapper.toEntity(request);
        
        // Then
        assertEquals(request.getCustomerId(), order.getCustomerId());
        assertEquals(request.getShippingAddressId(), order.getShippingAddressId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getOrderItems().size());
        assertNull(order.getId()); // Should be ignored
    }
    
    @Test
    void shouldMapOrderToOrderResponse() {
        // Given
        Order order = createSampleOrder();
        
        // When
        OrderResponse response = mapper.toResponse(order);
        
        // Then
        assertEquals(order.getId(), response.getOrderId());
        assertEquals(order.getCustomerId(), response.getCustomerId());
        assertEquals(order.getTotalAmount(), response.getTotalAmount());
        assertEquals(1, response.getItems().size());
    }
    
    @Test
    void shouldMapOrderToOrderCreatedEvent() {
        // Given
        Order order = createSampleOrder();
        
        // When
        OrderCreatedEvent event = mapper.toEvent(order);
        
        // Then
        assertEquals(order.getId(), event.getOrderId());
        assertEquals(order.getTotalAmount(), event.getTotalAmount());
        assertEquals(1, event.getItems().size());
    }
}
```

### Service Tests

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private ProductServiceClient productServiceClient;
    
    @Mock
    private OrderMapper orderMapper;
    
    @Mock
    private PriceCalculator priceCalculator;
    
    @Mock
    private OrderEventProducer eventProducer;
    
    @InjectMocks
    private OrderServiceImpl orderService;
    
    @Test
    void shouldCreateOrderSuccessfully() {
        // Given
        CreateOrderRequest request = createSampleRequest();
        ProductResponse product = createProductResponse();
        
        when(productServiceClient.getProductsByIds(any()))
            .thenReturn(List.of(product));
        when(priceCalculator.calculateSubtotal(any(), any()))
            .thenReturn(BigDecimal.valueOf(20));
        when(priceCalculator.calculateTotalAmount(any()))
            .thenReturn(BigDecimal.valueOf(40));
        when(orderRepository.save(any(Order.class)))
            .thenReturn(createSampleOrder());
        when(orderMapper.toResponse(any()))
            .thenReturn(createSampleResponse());
        
        // When
        OrderResponse response = orderService.createOrder(request);
        
        // Then
        verify(orderRepository).save(any(Order.class));
        verify(eventProducer).sendOrderCreatedEvent(any());
        assertEquals(OrderStatus.PENDING, response.orderStatus());
    }
    
    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        CreateOrderRequest request = createSampleRequest();
        when(productServiceClient.getProductsByIds(any()))
            .thenThrow(FeignException.NotFound.class);
        
        // When & Then
        assertThrows(ProductNotFoundException.class, 
            () -> orderService.createOrder(request));
        
        verify(orderRepository, never()).save(any());
    }
    
    @Test
    void shouldThrowExceptionWhenProductInactive() {
        // Given
        ProductResponse inactiveProduct = createProductResponse();
        inactiveProduct.setActive(false);
        
        when(productServiceClient.getProductsByIds(any()))
            .thenReturn(List.of(inactiveProduct));
        
        // When & Then
        assertThrows(ProductInactiveException.class,
            () -> orderService.createOrder(request));
    }
    
    @Test
    void shouldThrowExceptionWhenOrderIsEmpty() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(List.of());
        
        // When & Then
        assertThrows(EmptyOrderException.class,
            () -> orderService.createOrder(request));
    }
}
```

### Kafka Producer Tests

```java
@ExtendWith(MockitoExtension.class)
class OrderEventProducerTest {
    
    @Mock
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private OrderEventProducer producer;
    
    @Test
    void shouldSendOrderCreatedEvent() {
        // Given
        OrderCreatedEvent event = createSampleEvent();
        ListenableFuture<SendResult<String, OrderCreatedEvent>> future = 
            new CompletableFuture<>();
        SendResult<String, OrderCreatedEvent> result = mock(SendResult.class);
        
        when(kafkaTemplate.send(any(), any(), any()))
            .thenReturn(future);
        
        // When
        producer.sendOrderCreatedEvent(event);
        
        // Then
        verify(kafkaTemplate).send(
            eq("order-created"),
            eq(event.getOrderId().toString()),
            eq(event)
        );
    }
    
    @Test
    void shouldHandleSendFailure() {
        // Given
        OrderCreatedEvent event = createSampleEvent();
        
        when(kafkaTemplate.send(any(), any(), any()))
            .thenThrow(new KafkaException("Connection failed"));
        
        // When - should not throw exception (at-least-once delivery)
        assertDoesNotThrow(() -> producer.sendOrderCreatedEvent(event));
        
        // Then
        verify(kafkaTemplate).send(any(), any(), any());
    }
}
```

## Integration Tests

### Create Order API Integration Test

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
class OrderControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ProductServiceClient productServiceClient;
    
    @Test
    @Transactional
    void shouldCreateOrderSuccessfully() throws Exception {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        
        CreateOrderRequest request = CreateOrderRequest.builder()
            .customerId(customerId)
            .shippingAddressId(UUID.randomUUID())
            .items(List.of(
                new CreateOrderItemRequest(productId, 2)
            ))
            .build();
        
        ProductResponse product = new ProductResponse();
        product.setId(productId);
        product.setPrice(BigDecimal.valueOf(50));
        product.setActive(true);
        
        when(productServiceClient.getProductsByIds(any()))
            .thenReturn(List.of(product));
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.orderId").exists())
            .andExpect(jsonPath("$.customerId").value(customerId.toString()))
            .andExpect(jsonPath("$.orderStatus").value("PENDING"))
            .andExpect(jsonPath("$.totalAmount").value(100))
            .andExpect(jsonPath("$.items[0].productId").value(productId.toString()))
            .andExpect(jsonPath("$.items[0].quantity").value(2));
    }
    
    @Test
    void shouldReturn400WhenRequestInvalid() throws Exception {
        // Given - invalid request (missing customerId)
        String invalidRequest = """
            {
                "shippingAddressId": "123e4567-e89b-12d3-a456-426614174001",
                "items": []
            }
            """;
        
        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
```

### Database Persistence Test

```java
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class OrderRepositoryTest {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Test
    void shouldSaveOrderWithItems() {
        // Given
        Order order = Order.builder()
            .customerId(UUID.randomUUID())
            .shippingAddressId(UUID.randomUUID())
            .status(OrderStatus.PENDING)
            .totalAmount(BigDecimal.valueOf(100))
            .build();
        
        OrderItem item = OrderItem.builder()
            .productId(UUID.randomUUID())
            .quantity(2)
            .unitPrice(BigDecimal.valueOf(50))
            .subtotal(BigDecimal.valueOf(100))
            .build();
        
        order.addOrderItem(item);
        
        // When
        Order savedOrder = orderRepository.save(order);
        
        // Then
        assertNotNull(savedOrder.getId());
        assertNotNull(savedOrder.getCreatedAt());
        assertEquals(1, savedOrder.getOrderItems().size());
        assertNotNull(savedOrder.getOrderItems().get(0).getId());
    }
    
    @Test
    void shouldDeleteOrderItemsWhenCascadeEnabled() {
        // Given
        Order order = createSampleOrder();
        Order savedOrder = orderRepository.save(order);
        UUID orderId = savedOrder.getId();
        
        // When
        orderRepository.delete(savedOrder);
        
        // Then
        assertTrue(orderRepository.findById(orderId).isEmpty());
    }
}
```

### Kafka Event Published Test

```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"order-created"})
@TestPropertySource(locations = "classpath:application-test.yml")
class OrderKafkaIntegrationTest {
    
    @Autowired
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    
    @Autowired
    private MockMvc mockMvc;
    
    private final List<OrderCreatedEvent> receivedEvents = new ArrayList<>();
    
    @KafkaListener(topics = "order-created", groupId = "test-group")
    public void listen(OrderCreatedEvent event) {
        receivedEvents.add(event);
    }
    
    @Test
    void shouldPublishOrderCreatedEvent() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        
        CreateOrderRequest request = createSampleRequest(orderId, productId);
        
        ProductResponse product = createProductResponse(productId);
        when(productServiceClient.getProductsByIds(any()))
            .thenReturn(List.of(product));
        
        // When
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
        
        // Then - wait for event
        await().atMost(Duration.ofSeconds(5))
            .until(() -> receivedEvents.size() == 1);
        
        OrderCreatedEvent event = receivedEvents.get(0);
        assertEquals(orderId, event.getOrderId());
        assertEquals(1, event.getItems().size());
    }
}
```

## Testing Best Practices

- **Use `@ExtendWith(MockitoExtension.class)`** for unit tests
- **Mock external dependencies** (Product Service, Kafka)
- **Use `@SpringBootTest`** with `@AutoConfigureMockMvc` for integration tests
- **Use `@DataJpaTest`** for repository tests
- **Use `@EmbeddedKafka`** for Kafka integration tests
- **Use Testcontainers** for real Kafka and PostgreSQL instances
- **Aim for 80%+ code coverage** on business logic
- **Test edge cases**: invalid input, null values, exceptions
- **Use `assertThrows`** for exception testing

### Test Coverage Targets

- **Mapper**: 100% (straightforward mappings)
- **Service**: 80%+ (all success and failure scenarios)
- **Controller**: 70%+ (all endpoints and error cases)
- **Repository**: 80%+ (CRUD operations)
- **Integration**: Key user journeys (create order flow)

### Testing Tasks

- [ ] Add test dependencies (JUnit 5, Mockito, Testcontainers)
- [ ] Write mapper tests (100% coverage)
- [ ] Write service tests (all scenarios)
- [ ] Write Kafka producer tests
- [ ] Write controller integration tests
- [ ] Write repository tests
- [ ] Write Kafka integration test
- [ ] Configure JaCoCo for code coverage

---

# Future APIs

- [ ] GET /api/orders/{orderId}
- [ ] GET /api/orders/customer/{customerId}
- [ ] PATCH /api/orders/{orderId}/cancel
- [ ] PATCH /api/orders/{orderId}/status

---

# Future Improvements

## Additional Service Consumers

- [ ] **Inventory Service Consumer** - Reserve stock when order is created
  ```java
  @KafkaListener(topics = "order-created")
  public void handleOrderCreated(OrderCreatedEvent event) {
      inventoryService.reserveStock(event.getItems());
      kafkaTemplate.send("inventory-reserved", event.getOrderId(), 
          new InventoryReservedEvent(event.getOrderId()));
  }
  ```

- [ ] **Payment Service Consumer** - Charge customer payment
  ```java
  @KafkaListener(topics = "order-created")
  public void handleOrderCreated(OrderCreatedEvent event) {
      PaymentResult result = paymentService.chargeCustomer(
          event.getCustomerId(), 
          event.getTotalAmount()
      );
      kafkaTemplate.send("payment-completed", event.getOrderId(), 
          new PaymentCompletedEvent(event.getOrderId(), result.getTransactionId()));
  }
  ```

- [ ] **Notification Service Consumer** - Send order confirmation email/SMS
  ```java
  @KafkaListener(topics = "order-created")
  public void handleOrderCreated(OrderCreatedEvent event) {
      notificationService.sendOrderConfirmation(
          event.getCustomerId(),
          event.getOrderId()
      );
  }
  ```

## Resilience Patterns

### Saga Pattern for Distributed Transactions

Use **Saga** pattern to manage distributed transactions across services:

```java
@Component
public class OrderSaga {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @KafkaListener(topics = "order-created")
    public void startSaga(OrderCreatedEvent event) {
        // Step 1: Reserve inventory
        try {
            reserveInventory(event);
            kafkaTemplate.send("inventory-reserved", event.getOrderId(), event);
        } catch (InventoryReservationFailedException e) {
            compensateOrder(event); // Cancel order
        }
    }
    
    @KafkaListener(topics = "inventory-reserved")
    public void processPayment(OrderCreatedEvent event) {
        try {
            chargePayment(event);
            kafkaTemplate.send("payment-completed", event.getOrderId(), event);
        } catch (PaymentFailedException e) {
            compensateInventory(event); // Release inventory
            compensateOrder(event); // Cancel order
        }
    }
    
    @KafkaListener(topics = "payment-completed")
    public void confirmOrder(OrderCreatedEvent event) {
        orderService.updateStatus(event.getOrderId(), OrderStatus.CONFIRMED);
        kafkaTemplate.send("order-confirmed", event.getOrderId(), event);
    }
    
    // Compensation methods
    private void compensateOrder(OrderCreatedEvent event) {
        orderService.updateStatus(event.getOrderId(), OrderStatus.CANCELLED);
    }
    
    private void compensateInventory(OrderCreatedEvent event) {
        inventoryService.releaseStock(event.getOrderId());
    }
}
```

### Transactional Outbox Pattern

Ensure reliable event publishing without dual-write problems:

```java
@Entity
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String aggregateType;  // "Order"
    private String aggregateId;    // orderId
    private String eventType;      // "OrderCreatedEvent"
    @Lob
    private String payload;        // JSON payload
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private int retryCount;
    private String errorMessage;
}

@Component
public class OutboxProcessor {
    
    @Scheduled(fixedDelay = 5000) // Every 5 seconds
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findTop100ByPublishedAtIsNullOrderByCreatedAt();
        
        for (OutboxEvent event : pendingEvents) {
            try {
                kafkaTemplate.send(event.getEventType(), event.getPayload());
                event.setPublishedAt(LocalDateTime.now());
                outboxRepository.save(event);
            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                event.setErrorMessage(e.getMessage());
                outboxRepository.save(event);
            }
        }
    }
}
```

### Dead Letter Queue (DLQ) Pattern

Handle permanently failed messages:

```yaml
spring:
  kafka:
    consumer:
      properties:
        max.poll.retries: 3
        retry.backoff.ms: 1000
    listener:
      missing-topics-fatal: false
    template:
      default-topic: order-created
    producer:
      properties:
        # Send to DLQ after retries exhausted
        default.replication.factor: 3
```

```java
@KafkaListener(
    topics = "order-created-dlq",
    groupId = "order-service-dlq"
)
public void handleDLQ(ConsumerRecord<String, OrderCreatedEvent> record) {
    log.error("Received DLQ message: {}", record);
    
    // Alert operations team
    alertService.sendAlert("DLQ message received", record);
    
    // Manual intervention or automated recovery
    if (canRetry(record)) {
        retryProcessing(record);
    } else {
        storeForManualReview(record);
    }
}
```

### Retry Mechanism with Exponential Backoff

```java
@Service
public class ProductServiceClientFallback implements ProductServiceClient {
    
    @Retryable(
        value = { FeignException.ServiceUnavailable.class, FeignException.Connection.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2) // 1s, 2s, 4s
    )
    @Override
    public ProductResponse getProductById(UUID productId) {
        return productServiceClient.getProductById(productId);
    }
    
    @Recover
    public ProductResponse recover(FeignException ex, UUID productId) {
        log.error("Failed to fetch product after retries: {}", productId, ex);
        throw new ProductServiceUnavailableException("Product service unavailable");
    }
}
```

### Circuit Breaker Pattern

```java
@Service
@Slf4j
public class ProductServiceClient {
    
    private final ProductServiceClient productServiceClient;
    
    @CircuitBreaker(
        name = "productService",
        fallbackMethod = "getProductByIdFallback"
    )
    @Override
    public ProductResponse getProductById(UUID productId) {
        return productServiceClient.getProductById(productId);
    }
    
    public ProductResponse getProductByIdFallback(UUID productId, Exception e) {
        log.warn("Circuit breaker activated for product: {}", productId);
        throw new ProductServiceUnavailableException();
    }
}
```

### Distributed Tracing

Implement distributed tracing with OpenTelemetry:

```java
@Configuration
public class TracingConfig {
    
    @Bean
    public KafkaTracing kafkaTracing(Tracer tracer) {
        return KafkaTracing.create(tracer);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// Usage
@GetMapping("/api/products/{id}")
@NewSpan("get-product")
public ProductResponse getProduct(@SpanTag("product.id") @PathVariable UUID id) {
    return productService.getProduct(id);
}
```

### Idempotency Pattern

Ensure idempotent order creation:

```java
@Component
public class IdempotencyFilter {
    
    @Autowired
    private IdempotencyRepository idempotencyRepository;
    
    public boolean isProcessed(String requestId) {
        return idempotencyRepository.existsById(requestId);
    }
    
    @Transactional
    public void markProcessed(String requestId, Order order) {
        IdempotencyRecord record = new IdempotencyRecord();
        record.setRequestId(requestId);
        record.setOrderId(order.getId());
        record.setProcessedAt(LocalDateTime.now());
        idempotencyRepository.save(record);
    }
}

// Controller
@PostMapping
public ResponseEntity<OrderResponse> createOrder(
        @RequestHeader("X-Request-ID") String requestId,
        @Valid @RequestBody CreateOrderRequest request) {
    
    if (idempotencyFilter.isProcessed(requestId)) {
        return ResponseEntity.ok().build(); // Return existing order
    }
    
    OrderResponse response = orderService.createOrder(request);
    idempotencyFilter.markProcessed(requestId, response);
    
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

## Monitoring & Observability

- [ ] **Add Micrometer metrics** for order processing time, error rates
  ```java
  @Bean
  public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
      return registry -> registry.config().commonTags(
          "application", "order-service",
          "region", "us-east-1"
      );
  }
  ```

- [ ] **Add health indicators** for Kafka and database
  ```java
  @Bean
  public HealthIndicator kafkaHealthIndicator(KafkaTemplate template) {
      return () -> {
          try {
              template.send("health-check", "test");
              return Health.up().build();
          } catch (Exception e) {
              return Health.down().withException(e).build();
          }
      };
  }
  ```

- [ ] **Add structured logging** with correlation IDs
  ```java
  @Component
  public class CorrelationIdFilter extends OncePerRequestFilter {
      @Override
      protected void doFilterInternal(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      FilterChain filterChain) 
              throws ServletException, IOException {
          MDC.put("correlationId", UUID.randomUUID().toString());
          try {
              filterChain.doFilter(request, response);
          } finally {
              MDC.clear();
          }
      }
  }
  ```

- [ ] **Add OpenTelemetry** for distributed tracing across services

## Performance Optimizations

- [ ] **Add Redis caching** for product prices (reduce Product Service calls)
  ```java
  @Cacheable(value = "product-prices", key = "#productId")
  public BigDecimal getProductPrice(UUID productId) {
      return productServiceClient.getProductById(productId).getPrice();
  }
  ```

- [ ] **Use connection pooling** (HikariCP - default in Spring Boot)

- [ ] **Add database indexes** on frequently queried fields
  ```sql
  CREATE INDEX idx_orders_customer_created ON orders(customer_id, created_at DESC);
  CREATE INDEX idx_orders_status ON orders(status) WHERE status != 'CONFIRMED';
  ```

- [ ] **Implement pagination** for GET endpoints
  ```java
  @GetMapping
  public Page<OrderResponse> getOrders(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "20") int size) {
      return orderService.getOrders(PageRequest.of(page, size));
  }
  ```

- [ ] **Use async processing** for non-critical operations
  ```java
  @Async
  public CompletableFuture<Void> sendAnalyticsEvent(Order order) {
      analyticsService.trackOrderCreated(order);
      return CompletableFuture.completedFuture(null);
  }
  ```

## Security Enhancements

- [ ] **Add OAuth2/JWT authentication**
  ```java
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      return http.authorizeHttpRequests(auth -> auth
          .requestMatchers("/api/orders/**").hasRole("USER")
          .anyRequest().permitAll()
      )
      .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
      .build();
  }
  ```

- [ ] **Implement rate limiting** on POST endpoint
  ```java
  @RateLimiter(name = "createOrder", fallbackMethod = "rateLimitFallback")
  public OrderResponse createOrder(CreateOrderRequest request) {
      return orderService.createOrder(request);
  }
  ```

- [ ] **Add request validation** for suspicious patterns (SQL injection, XSS)

- [ ] **Implement CORS** configuration with whitelist

## Data Management

- [ ] **Add soft deletes** for orders
  ```java
  @SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
  @Where(clause = "deleted = false")
  public class Order { ... }
  ```

- [ ] **Implement audit logging** with Hibernate Envers
  ```java
  @Audited
  @Entity
  public class Order { ... }
  ```

- [ ] **Add data archival strategy** for old orders

## Documentation

- [ ] **Generate OpenAPI documentation** with Swagger
- [ ] **Add API examples** with request/response samples
- [ ] **Document error codes** and meanings
- [ ] **Create architecture decision records (ADRs)** for key decisions

## DevOps & Deployment

- [ ] **Add Kubernetes deployment** manifests
  ```yaml
  apiVersion: apps/v1
  kind: Deployment
  metadata:
    name: order-service
  spec:
    replicas: 3
    template:
      spec:
        containers:
        - name: order-service
          image: order-service:latest
          resources:
            requests:
              memory: "512Mi"
              cpu: "500m"
            limits:
              memory: "1Gi"
              cpu: "1000m"
  ```

- [ ] **Configure auto-scaling** based on CPU/memory usage

- [ ] **Add health checks** for liveness and readiness probes

- [ ] **Set up CI/CD pipeline** with GitHub Actions

- [ ] **Add logging aggregation** with ELK stack

## Future Improvements Summary

**Priority:**
1. **High**: Retry mechanism, circuit breaker, DLQ, idempotency
2. **Medium**: Saga pattern, distributed tracing, monitoring
3. **Low**: Performance optimizations, advanced features

**Estimated Timeline:**
- Phase 1 (High Priority): 2-3 sprints
- Phase 2 (Medium Priority): 3-4 sprints  
- Phase 3 (Low Priority): Ongoing enhancements

---

# Development Sequence

- [x] Entities
- [ ] Repository
- [ ] Request DTOs
- [ ] Response DTOs
- [ ] Kafka Event DTOs
- [ ] Mapper
- [ ] Service
- [ ] Kafka Producer
- [ ] Controller
- [ ] Exception Handling
- [ ] Testing
- [ ] Additional APIs

## Architecture Flow Diagram

```
┌─────────┐
│ Client  │
└────┬────┘
     │ POST /orders
     ▼
┌─────────────────┐
│  Order Service  │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌────────┐  ┌──────────────┐
│  REST  │  │    Kafka     │
│        │  │  (Async)     │
└───┬────┘  └──────┬───────┘
    │               │
    ▼               ▼
┌─────────────┐  ┌──────────┐
│   Product   │  │order-    │
│   Service   │  │created   │
│(validate +  │  │  event   │
│   price)    │  └────┬─────┘
└─────────────┘       │
                      │
         ┌────────────┼────────────┐
         │            │            │
         ▼            ▼            ▼
   ┌───────────┐ ┌──────────┐ ┌──────────┐
   │ Inventory │ │ Payment  │ │Notification│
   │ Service   │ │ Service  │ │  Service  │
   │(reserve   │ │(charge   │ │(send email│
   │  stock)   │ │ customer)│ │  confirm) │
   └─────┬─────┘ └────┬─────┘ └─────┬────┘
         │            │             │
         ▼            ▼             ▼
   ┌───────────┐ ┌──────────┐ ┌──────────┐
   │inventory- │ │ payment- │ │notifica- │
   │  reserved │ │completed │ │  tion-   │
   │           │ │          │ │  sent    │
   └─────┬─────┘ └────┬─────┘ └─────┬────┘
         │            │             │
         └────────────┼─────────────┘
                      │
                      ▼
            ┌─────────────────┐
            │  Order Service  │
            │(update status:  │
            │  CONFIRMED)     │
            └─────────────────┘
```
