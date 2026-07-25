# Gateway Errors

## Error 1

### Error

```
Port 8080 already in use
```

### Cause

Tomcat9 Windows Service was already running on port 8080.

### Diagnosis

```bash
netstat -ano | findstr :8080
```

Result

```
LISTENING PID 5380
```

Identify process

```bash
tasklist /FI "PID eq 5380"
```

Output

```
Tomcat9.exe
```

### Fix

```powershell
Stop-Service Tomcat9
```

Verify

```bash
netstat -ano | findstr :8080
```

Expected

```
No LISTENING process
```

---

## Error 2

### Message

```
Gateway not registering with Eureka
```

### Cause

Incorrect Eureka configuration.

### Fix

Verified

```yaml
spring:
  application:
    name: gateway-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

## Error 3

### Message

```
EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP...
```

### Cause

Eureka Self-Preservation Mode.

### Explanation

- Heartbeats dropped below threshold.
- Eureka stops expiring instances.
- Common during local development.

### Fix

No fix required.

(Optional)

```yaml
eureka:
  server:
    enable-self-preservation: false
```