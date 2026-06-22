# E-Commerce Platform - Backend Project Overview

## Project Structure
This is a **microservices-based e-commerce platform** built with **Spring Boot** and **Spring Cloud** using Java. The architecture follows the **service mesh pattern** with centralized configuration, service discovery, and API gateway. Backend is deployed using docker for each services to ``Render`` and frontend to ``Vercel``. 

---
## Services Overview

### 1. **API Gateway** (Port: 8080)
**Framework:** Spring Cloud Gateway MVC (Non-reactive)  
**Role:** Entry point for all client requests

**Key Features:**
- JWT authentication and authorization
- Token validation with fallback to refresh token from cookies
- Route traffic to microservices with load balancing
- CORS configuration (configurable origins, methods, headers)
- Cookie management (secure, HTTPOnly)
- Gateway secret for internal service communication
- Request header forwarding (Authorization, X-Gateway-Secret)

**Routes:**
```
/api/v1/user/**     → User Service
/api/v1/auth/**     → User Service (Authentication)
/api/v1/oauth2/**   → User Service (OAuth2)
/api/v1/product/**  → Product Service
/api/v1/cart-item/**  → Order Service
/api/v1/order/**    → Order Service
/api/v1/payment/**  → Order Service
```

**Configuration:** `CloudConfig/api-gateway.yaml`

---

### 2. **User Service** (Port: 8081)
**Database:** PostgreSQL (shared: `ecommerce`)  
**Role:** User authentication, registration, and OAuth2 integration

**Key Features:**
- JWT token generation (access & refresh tokens)
- OAuth2 integration (Google login)
- User registration and authentication
- Cookie management for token storage
- Tracing with Brave/Zipkin

**Dependencies:**
- Spring Data JPA
- PostgreSQL driver
- JWT (JJWT 0.12.3)
- OAuth2 Client
- MapStruct (DTO mapping)

**Key Models:**
- User (with OAuth2 provider info)

**Configuration:** `CloudConfig/user-service.yaml`

---

### 3. **Product Service** (Port: 8082)
**Database:** PostgreSQL (shared: `ecommerce`)  
**Role:** Product catalog management and search

**Key Features:**
- Product CRUD operations
- Category management
- Product specifications and attributes
- Internal APIs for other services (via gateway secret)
- OpenTelemetry tracing with OTLP metrics
- Source authentication (service-to-service verification)

**Dependencies:**
- Spring Data JPA
- PostgreSQL driver

**Key Models:**
- Product (with images, categories, specifications)
- Category
- ProductSpecification

**Controllers:**
- `ProductController` - Public endpoints
- `InternalController` - Internal service endpoints

**Configuration:** `CloudConfig/product-service.yaml`

---

### 4. **Order Service** (Port: 8083)
**Database:** PostgreSQL (shared: `ecommerce`)  
**Role:** Order management and payment processing

**Key Features:**
- Order creation and management
- Payment processing with Stripe integration
- Order status tracking
- Stripe webhook handling
- JWT validation for service-to-service calls
- Snowflake ID generation for distributed IDs
- Feign client for Product Service communication
- Source authentication (inter-service)

**Dependencies:**
- Spring Data JPA
- Stripe Java SDK (v32.0.0)
- hutool-core (utility library)
- JWT (JJWT 0.12.3)
- MapStruct

**Key Models:**
- Order (with order items)
- OrderItem
- Payment (with PaymentMethod & PaymentStatus)
- OrderStatus enum

**Key Services:**
- `OrderService` - Order business logic
- `PaymentService` - Payment processing
- `StripeService` - Stripe integration
- `JwtService` - Token validation

**Stripe Configuration:**
```yaml
stripe:
  secret-key: ${STRIPE_SECRET_KEY}
  publishable-key: ${STRIPE_PUBLISHABLE_KEY}
  webhook-secret: ${STRIPE_WEBHOOK_SECRET}
```

**Configuration:** `CloudConfig/order-service.yaml`

---

### 5. **Eureka Server** (Port: 8761)
**Framework:** Spring Cloud Netflix Eureka  
**Role:** Service registry and discovery

**Key Features:**
- Service registration and deregistration
- Health check monitoring
- Client-side service discovery
- Prometheus metrics export
- OpenTelemetry integration

**Configuration:** `CloudConfig/eureka-server.yaml`

---

### 6. **Config Server** (Port: 8888)
**Framework:** Spring Cloud Config Server  
**Role:** Centralized configuration management

**Key Features:**
- Centralized YAML configuration for all services
- Dynamic configuration refresh
- Prometheus metrics export

**Configuration Files:**
```
CloudConfig/
├── application.yaml (default config for all services)
├── api-gateway.yaml
├── eureka-server.yaml
├── user-service.yaml
├── product-service.yaml
├── order-service.yaml
├── cart-service.yaml
├── notification-service.yaml
```

**Configuration:** `CloudConfig/application.yaml`

---

## Infrastructure & Dependencies

### Database
- **PostgreSQL** - Shared database for all services
- **Host:** `${DB_HOST:localhost}`
- **Port:** `${DB_PORT:5432}`
- **Database:** `ecommerce`
- **Timezone:** UTC (configured via connection-init-sql)

### Payment
- **Stripe** - Payment processing and webhooks

### External Services
- **Google OAuth2** - Social login integration
---

## Build & Compilation

### Build Tool
- **Maven** - All services use Maven for build management
- **Parent POM:** Spring Boot 4.0.1

### Java Version
- **Java 25** - All services

### Compiler Plugins
- **Annotation Processing:**
  - Lombok
  - Spring Boot Configuration Processor
  - MapStruct Processor
  - Lombok-MapStruct Binding

---

## Configuration Management

### Environment Variables
All sensitive configurations use environment variables with defaults:

```yaml
# JWT
APP_GATEWAY_SECRET=${APP_GATEWAY_SECRET}
APP_SERVICE_SECRET=${APP_SERVICE_SECRET}

# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ecommerce
DB_USERNAME=postgres
DB_PASSWORD=postgres

# Stripe
STRIPE_SECRET_KEY=${STRIPE_SECRET_KEY}
STRIPE_PUBLISHABLE_KEY=${STRIPE_PUBLISHABLE_KEY}
STRIPE_WEBHOOK_SECRET=${STRIPE_WEBHOOK_SECRET}

# Google OAuth2
GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
GOOGLE_REDIRECT_URI=${GOOGLE_REDIRECT_URI}

# Mail (Notification Service)
SPRING_MAIL_HOST=${SPRING_MAIL_HOST}
SPRING_MAIL_PORT=${SPRING_MAIL_PORT}
SPRING_MAIL_USERNAME=${SPRING_MAIL_USERNAME}
SPRING_MAIL_PASSWORD=${SPRING_MAIL_PASSWORD}

# Tracing
ZIPKIN_ENDPOINT=http://localhost:9411/v2/spans
ZIPKIN_ENABLE=true
MANAGEMENT_OPEN_TELEMETRY_TRACING_ENDPOINT=http://localhost:4318/v1/traces
```

---

## CORS Configuration

### Default Settings
```yaml
cors:
  enabled: true
  allowed-origins: http://localhost:4000 (Gateway)
  allowed-origin-patterns: http://localhost:* (Services)
  allowed-methods: GET, POST, PUT, DELETE, OPTIONS
  allowed-headers: *
  exposed-headers: Authorization, Access-Control-Allow-Origin, Access-Control-Allow-Credentials
  allow-credentials: true
  max-age: 3600
```

---

## Docker & Deployment Notes

### Service Ports Summary
| Service         | Port | Database   | Auth Method          |
|-----------------|------|------------|----------------------|
| API Gateway     | 8080 | N/A        | JWT & Gateway Secret |
| User Service    | 8081 | PostgreSQL | JWT & Gateway Secret |
| Product Service | 8082 | PostgreSQL | Gateway Secret       |
| Order Service   | 8083 | PostgreSQL | JWT & Gateway Secret |
| Eureka Server   | 8761 | N/A        | N/A                  |
| Config Server   | 8888 | N/A        | N/A                  |

---

## Key Technologies Stack

| Category      | Technology     | Version                 |
|---------------|----------------|-------------------------|
| Framework     | Spring Boot    | 4.0.1                   |
| Cloud         | Spring Cloud   | 2025.1.0                |
| Language      | Java           | 25                      |
| Database      | PostgreSQL     | 12+                     |
| Build Tool    | Maven          | 3.6+                    |
| JWT           | JJWT           | 0.12.3                  |
| Mapping       | MapStruct      | 1.5.5.Final             |
| ORM           | Hibernate/JPA  | Via Spring Data         |
| Payment       | Stripe         | 32.0.0                  |
| Annotations   | Lombok         | Latest                  |
| Discovery     | Netflix Eureka | Spring Cloud integrated |

---

## Summary

This is a **production-ready microservices architecture** with:
- Centralized configuration management
- Service-to-service communication via Feign
- JWT-based authentication & authorization
- OAuth2 social login support
- Payment processing with Stripe
- Distributed tracing & observability
- Health checks & metrics collection
- CORS configuration
- Comprehensive error handling

The architecture is scalable, resilient, and follows microservices best practices.

