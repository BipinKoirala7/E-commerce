# Obsidian - E-Commerce Platform

A premium e-commerce platform featuring a modern full-stack architecture with microservices backend and a responsive Next.js frontend. **"A place where only premium is available."**

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Services Documentation](#services-documentation)
- [API Endpoints](#api-endpoints)
- [Deployment](#deployment)
- [Development](#development)
- [Future Enhancements](#future-enhancements)

---

## 🎯 Project Overview

**Obsidian** is a full-featured e-commerce platform designed for premium product sales. It combines a robust microservices backend with a modern, responsive frontend to deliver a seamless shopping experience.

### Key Features

- ✅ User authentication & OAuth2 (Google login)
- ✅ Product catalog with categories & specifications
- ✅ Shopping cart & wishlist management
- ✅ Order processing & payment (Stripe integration)
- ✅ JWT-based security with refresh tokens
- ✅ Distributed tracing & observability
- ✅ Service-to-service authentication
- ✅ CORS configuration
- ✅ Metrics & health monitoring
- ✅ Centralized configuration management

---

## 🛠️ Technology Stack

### Backend

| Component                            | Technology               | Version                |
|--------------------------------------|--------------------------|------------------------|
| **Framework**                        | Spring Boot              | 4.0.1                  |
| **Cloud Framework**                  | Spring Cloud             | 2025.1.0               |
| **Language**                         | Java                     | 25                     |
| **Database**                         | PostgreSQL               | 12+                    |
| **Database ORM**                     | Hibernate/JPA            | Via Spring Data        |
| **Build Tool**                       | Maven                    | 3.6+                   |
| **API Gateway**                      | Spring Cloud Gateway MVC | 2025.1.0               |
| **Service Discovery**                | Netflix Eureka           | Spring Cloud           |
| **Config Server**                    | Spring Cloud Config      | 2025.1.0               |
| **JWT Library**                      | JJWT                     | 0.12.3                 |
| **DTO Mapping**                      | MapStruct                | 1.5.5.Final            |
| **Annotations**                      | Lombok                   | Latest                 |
| **Payment Processing**               | Stripe Java SDK          | 32.0.0                 |
| **Code Generation**                  | Lombok, MapStruct        | Latest                 |
| **Distributed Tracing**              | Brave + Zipkin           | Spring Boot integrated |
| **Observability**                    | OpenTelemetry (OTLP)     | Spring Boot integrated |
| **Metrics**                          | Prometheus + Micrometer  | Spring Boot integrated |
| **Message Broker**                   | RabbitMQ                 | 3.8+                   |
| **Service-to-Service Communication** | OpenFeign                | Spring Cloud           |

### Frontend

| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Next.js | 16.2.4 |
| **UI Library** | React | 19.2.3 |
| **Language** | TypeScript | 5 |
| **Styling** | Tailwind CSS | 4 |
| **HTTP Client** | Axios | 1.13.6 |
| **State Management** | Zustand | 5.0.11 |
| **Data Fetching** | SWR | 2.4.1 |
| **Notifications** | React Hot Toast | 2.6.0 |
| **Icons** | React Icons | 5.5.0 |
| **Animation** | Motion | 12.34.0 |
| **Linting** | ESLint | 9 |

### Infrastructure

- **Web Server:** Nginx (reverse proxy)
- **Container Orchestration:** Docker (planned)
- **Distributed IDs:** Snowflake ID (used in Order Service)
- **External Auth:** Google OAuth2
- **Payment Gateway:** Stripe

---

## 🏗️ Architecture
### Architectural Patterns

1. **Microservices Pattern:** Each service has a specific responsibility
2. **API Gateway Pattern:** Single entry point for all client requests
3. **Service Discovery:** Eureka server for dynamic service registration
4. **Centralized Configuration:** Spring Cloud Config for environment-specific configs
5. **JWT-based Security:** Token-based authentication with refresh tokens
6. **Distributed Tracing:** Trace requests across multiple services
7. **Circuit Breaker Pattern** (via Spring Cloud):FailSafe inter-service communication
8. **Service-to-Service Authentication:** Gateway secret for internal communication

---

## 🚀 Getting Started
### Prerequisites

- **Java 25** - Required for backend services
- **Node.js 18+** - Required for frontend
- **PostgreSQL 12+** - Database
- **Docker** _(optional)_ - For containerized deployment
- **Git** - Version control

### Backend Setup

1. **Start PostgreSQL**
   ```bash
   # Create database if not exists
   createdb ecommerce

2. **Start Eureka Server**

    `cd Backend/EurekaServer
    mvn spring-boot:run`
3. **Accessible at: http://localhost:8761**

   `Start Config Server`
4. **Accessible at: http://localhost:8888**

   `cd Backend/ConfigServer
   mvn spring-boot:run
   Start Core Services (in order)`
5. **Terminal 1 - User Service**

    `cd Backend/UserService
    mvn spring-boot:run`
6. **Terminal 2 - Product Service**

    `cd Backend/ProductService
    mvn spring-boot:run`
7. **Terminal 3 - Order Service**

    `cd Backend/OrderService
    mvn spring-boot:run`
8. **Terminal 4 - Cart Service**

    `cd Backend/CartService
    mvn spring-boot:run`
9. ** Terminal 5 - API Gateway**

   `Start API Gateway (last)
   cd Backend/APIGateway
   mvn spring-boot:run`

### Frontend Setup
1. **Install dependencies**
   ```bash
   cd Frontend
   npm install
   ```
2. **Create .env.local file**
   ```bash
   # .env.local
   NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
   ```
3. **Start development server**
   ```bash
   npm run dev
   # Accessible at: http://localhost:3000
   ```
4. **Build for production**
   ```bash
   npm run build
   npm start
   ```

### Environment Variables
Create a .env file in the root and Backend services directories:
```bash
    # Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ecommerce
DB_USERNAME=postgres
DB_PASSWORD=postgres

# JWT
APP_GATEWAY_SECRET=your-gateway-secret-key
APP_SERVICE_SECRET=your-service-secret-key

# Stripe
STRIPE_SECRET_KEY=sk_test_your_key
STRIPE_PUBLISHABLE_KEY=pk_test_your_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret

# Google OAuth2
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-client-secret
GOOGLE_REDIRECT_URI=http://localhost:8080/api/v1/oauth2/callback/google

# Tracing (Optional)
ZIPKIN_ENDPOINT=http://localhost:9411/v2/spans
ZIPKIN_ENABLE=true
MANAGEMENT_OPEN_TELEMETRY_TRACING_ENDPOINT=http://localhost:4318/v1/traces
```

### Note: The project is still under development, and some features may not be fully implemented yet.
