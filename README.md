# Warehouse & Store Fulfillment System - Architectural Implementation

## Overview
This repository contains a reference implementation for a **Warehouse Management & Store Fulfillment System**. The primary goal is to demonstrate a robust, scalable architecture capable of handling complex domain rules (capacity, product limits) and legacy system integration without coupling.

The system is built using **Quarkus** and adheres to **Domain-Driven Design (DDD)** principles, structured as a Modular Monolith.

---

## Architectural Decisions

### 1. Architecture: Modular Monolith
**Context**: The requirement involves distinct but related domains (Warehouse, Store, Fulfillment) with moderate complexity.
**Decision**: We adopted a **Modular Monolith** approach with strictly enforced package boundaries, utilizing **Hexagonal Architecture (Ports & Adapters)**.
**Consequences**:
*   ✅ **Positive**: Low operational complexity (single deployment unit), shared type safety, and ease of refactoring.
*   ✅ **Positive**: Domain logic is isolated from framework details (Quarkus/Hibernate), making unit testing fast and pure.
*   ⚠️ **Trade-off**: Deploys as a single unit, but "Context Mapping" ensures modules can be split into Microservices later if scale demands it.

### 2. Legacy Integration: CDI Events (Observer Pattern)
**Context**: Store updates must be synchronized to a slow, file-based Legacy System.
**Decision**: Usage of **CDI Events** with `@Observes(during = TransactionPhase.AFTER_SUCCESS)`.
**Consequences**:
*   ✅ **Positive**: **Zero coupling** between the REST API / Domain Transaction and the Legacy System. API response times are unaffected by legacy file I/O.
*   ✅ **Positive**: **Transactional Integrity**—the event fires *only* if the main DB transaction commits successfully.
*   ⚠️ **Trade-off**: Asynchronous execution within the same JVM does not guarantee delivery if the application crashes immediately after commit (unlike a persistent message broker like RabbitMQ/Kafka). Given the scope, this infrastructure overhead was deemed unnecessary.

### 3. Validation Logic: Strategy Pattern
**Context**: Warehouses have complex, varying capacity and stock limit rules depending on their type or location.
**Decision**: Implemented a `WarehouseValidator` interface using the **Strategy Pattern**.
**Consequences**:
*   ✅ **Positive**: adheres to **Open/Closed Principle**. New validation rules can be added without modifying the core `Warehouse` entity or `CreateWarehouseUseCase`.
*   ✅ **Positive**: Complex "if/else" chains are replaced by polymorphic behavior.

---

## Test Strategy

Our testing strategy follows the **Test Pyramid**, aiming for high confidence with minimal feedback loops:

*   **Unit Tests (Junit 5 + Mockito)**: Focus on the **Domain Layer**. We verify business invariants (e.g., "Max 3 warehouses per store") without loading the Spring/Quarkus context. Fast execution.
*   **Integration Tests (`@QuarkusTest`)**: Focus on the **Adapter Layer**. We verify that REST endpoints correctly deserialize JSON and that Repositories correctly map Entities to the Database (H2/PostgreSQL).
*   **End-to-End / Component Tests**: Verify the full flow, including the CDI Event listeners for legacy sync.

---

## How to Run

### Requirements
*   JDK 17+
*   Maven 3.8+
*   Docker (Optional, for database/containerization)

### Local Development
```bash
cd ingka-java-code-assignment
./mvnw clean quarkus:dev
```

### Docker Support

**Running with Docker Compose (App + PostgreSQL)**:
1.  **Build the application**:
    ```bash
    cd ingka-java-code-assignment
    ./mvnw clean package -DskipTests
    cd ..
    ```

2.  **Start the services**:
    ```bash
    docker-compose up --build -d
    ```

3.  **Access the application**:
    *   API: `http://localhost:8080`
    *   Swagger UI: `http://localhost:8080/q/swagger-ui`
    *   Database: `localhost:15432`
