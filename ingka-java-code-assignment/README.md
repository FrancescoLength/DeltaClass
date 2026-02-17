# Warehouse Colocation Management System

## Overview

A simplified Warehouse Colocation Management System built with **Quarkus** and **Java 17**. This application manages the lifecycle of Locations, Stores, Warehouses, and Products, with a focus on domain validation and fulfillment orchestration.

## Architecture

The project follows a **Hexagonal Architecture** (Ports & Adapters) pattern for the Warehouse domain:

```
domain/
├── models/        → Domain entities (Warehouse, Location, Fulfillment)
├── ports/         → Interfaces defining operations and persistence contracts
├── usecases/      → Business logic orchestration
└── validation/    → Business rule enforcement
adapters/
├── database/      → JPA/Panache persistence implementations
└── restapi/       → REST API handlers (JAX-RS)
```

The **Store** and **Product** modules follow a simpler repository+resource pattern, consistent with the existing codebase structure.

## Tasks Completed

| Task | Description | Status |
|------|-------------|--------|
| **1. Location** | Implemented `LocationGateway.resolveByIdentifier` | ✅ |
| **2. Store** | Legacy system updates fire AFTER transaction commit (CDI Events) | ✅ |
| **3. Warehouse** | Create, Replace, Archive with full business validation | ✅ |
| **Bonus: Fulfillment** | Product-Warehouse-Store association with 3 constraint checks | ✅ |

## Key Design Decisions

- **CDI Events for Store (Task 2)**: Uses `@Observes(during = TransactionPhase.AFTER_SUCCESS)` to ensure legacy system calls happen only after a successful database commit.
- **Hexagonal Architecture (Task 3)**: The Warehouse module uses Ports & Adapters for clean separation between domain logic and infrastructure.
- **Contract-First API**: The Warehouse REST API is generated from an OpenAPI specification (`warehouse-openapi.yaml`), ensuring the contract is the single source of truth.
- **Validation Exception Mapping**: Business validation errors return HTTP 400 (Bad Request) via a global `ValidationExceptionMapper`.

## Running the Application

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker (for PostgreSQL in prod/dev mode)

### Development Mode
```bash
./mvnw quarkus:dev
```

### Running Tests
```bash
./mvnw test
```

### Code Coverage (JaCoCo)
```bash
./mvnw test
# Report generated at: target/jacoco-report/index.html
```

## Testing Strategy

- **Unit Tests**: Use Mockito for domain use cases and validators
- **Integration Tests**: Use `@QuarkusTest` with H2 in-memory database
- **Coverage**: JaCoCo configured with `quarkus-jacoco` extension

## Technology Stack

- **Quarkus 3.13.3** — Supersonic Subatomic Java framework
- **Hibernate ORM Panache** — Simplified JPA persistence
- **PostgreSQL** (production) / **H2** (testing)
- **JaCoCo** — Code coverage reporting
- **RestAssured** — REST API testing
- **Mockito** — Unit test mocking
