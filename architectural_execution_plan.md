# Architectural Execution Plan

## Executive Summary
This document outlines the strategic execution plan for delivering the technical assessment for the Java Architect position. The approach prioritizes architectural integrity, scalability, and clean code, leveraging the **Quarkus** framework and **Domain-Driven Design (DDD)** principles to demonstrate seniority.

## 1. Requirement & Constraint Mapping

### Functional Requirements
The assessment encompasses two primary work streams derived from "Ingka" and "FCS" contexts, which act as a unified "Fulfilment" domain.

*   **Location Management**: Implement `LocationGateway.resolveByIdentifier`.
*   **Store Management**: Guarantee eventual consistency for [Store](file:///media/francesco/Data/Coding/DeltaClass/ingka-java-code-assignment/.DS_Store) updates towards the legacy system (`LegacyStoreManagerGateway`). The legacy call must occur *only after* the database transaction commits.
*   **Warehouse Management**:
    *   **CRUD**: Create, Replace, Archive Warehouses.
    *   **Validations**: Business Unit uniqueness, Location validity, Capacity limits, Stock feasibility.
    *   **Replacement Logic**: Ensure new warehouse capacity >= old warehouse stock; stock levels must match.
    *   **Bonus (Project B)**: Implement specific fulfillment rules (Product -> Warehouse -> Store).

### Non-Functional Requirements (NFRs)
*   **Consistency & Data Integrity**: Vital for the Store legacy integration (Transaction delimitation).
*   **Testability**: High code coverage (>80%) with JUnit. Positive, negative, and error cases.
*   **Observability**: Structured Logging and Exception Handling.
*   **Standardization**: adherence to Clean Code, SOLID, and REST best practices.
*   **Scalability**: The application is a "monolith" but should be designed with modular boundaries (e.g., potential for extraction to microservices).

### Constraints
*   **Timeline**: 24 hours (Deadline: Tomorrow Afternoon).
*   **Tech Stack**: Java 17+, Quarkus (Panache, REST-Jackson), PostgreSQL (implied by typical Quarkus stacks, though H2 might be used for dev/test).
*   **AI Usage**: Permitted but must be supervised; code must not look "generated" or sparse.

## 2. System Design Strategy

### Architectural Pattern: Modular Monolith with Hexagonal Influence
We will treat the existing `monolith` package structure as a **Modular Monolith**.
*   **Domain Layout**: `location`, `stores`, `warehouse` are distinct modules.
*   **Hexagonal/Ports & Adapters**: we will strictly separate the **API** (Resources), **Domain** (Entities/Logic), and **Infrastructure** (Gateways/Repositories).
    *   *Note*: The existing code likely mixes these (Active Record with Panache). We will refactor where necessary to separate concerns (e.g., Service layer for business logic, Entity for state).

### Design Patterns & SOLID Principles
*   **Strategy Pattern**: For `Warehouse` validation rules. Instead of a massive `if-else` block, we will define a `WarehouseValidator` interface with implementations like `CapacityValidator`, `LocationValidator`.
*   **Observer / Application Event Pattern**: For the [Store](file:///media/francesco/Data/Coding/DeltaClass/ingka-java-code-assignment/.DS_Store) legacy integration. To decouple the DB commit from the legacy call, we will use Quarkus's `@Observes(during = TransactionPhase.AFTER_SUCCESS)` or a similar transactional event mechanism. This demonstrates high architectural awareness of transaction boundaries.
*   **Factory/Builder**: For complex `Warehouse` creation/replacement logic.
*   **Single Responsibility (SRP)**: Extract validation logic out of the Resource/Controller layer.
*   **Open/Closed (OCP)**: The validation chain should be extensible without modifying the core creation method.

## 3. Integration & Gap Analysis

**Projects A (`ingka`) & B (`fcs`) Relationship**:
Project B is a superset of Project A.
*   **Strategy**: We will focus execution on **Project B** methodology as the primary codebase, as it contains the broader scope (Cases Study + Bonus). Validating the [pom.xml](file:///media/francesco/Data/Coding/DeltaClass/ingka-java-code-assignment/pom.xml) confirms they are effectively the same application core.
*   **Gap to Close**:
    *   The [Store](file:///media/francesco/Data/Coding/DeltaClass/ingka-java-code-assignment/.DS_Store) requirement implies existing potentially buggy or "naive" code.
    *   The `Warehouse` replacement logic is complex state transition logic requiring transactional atomicity (Archive old + Create new).

## 4. Hourly Execution Roadmap (24-Hour Countdown)

**Phase 1: Scaffolding & Infrastructure (Now - T+2h)**
*   [ ] Unify project context (confirm Project B is the master).
*   [ ] Setup Git repository.
*   [ ] **Action**: Search for existing tests and run them (establish baseline).
*   [ ] **Action**: CI/CD Setup (GitHub Actions for Build + Test).
*   [ ] **Design**: Diagram the Modules (Mermaid).

**Phase 2: Core Architecture & Location/Store (T+2h - T+6h)**
*   [ ] **Task**: Implement `LocationGateway` (Warm-up).
*   [ ] **Task**: Refactor `StoreResource`.
    *   *Tech Impl*: Introduce an EventBus or dedicated Service with Transaction synchronization for the Legacy call.
*   [ ] verify with Tests.

**Phase 3: Warehouse Domain logic (T+6h - T+12h)**
*   [ ] **Design**: Interface for `WarehouseValidator`.
*   [ ] **Task**: Implement `create` and `replace` Use Cases.
*   [ ] **Task**: Implement Bonus constraints (Product/Store relation).
*   [ ] **Testing**: Write Unit Tests for each Validator.

**Phase 4: Case Study & Documentation (T+12h - T+16h)**
*   [ ] **Task**: Write [CASE_STUDY.md](file:///media/francesco/Data/Coding/DeltaClass/fcs-interview-code-assignment-main/case-study/CASE_STUDY.md) responses. Focus on "Senior/Architect" tone—cost attribution, distributed systems challenges, CAP theorem trade-offs.
*   [ ] **Task**: Write [QUESTIONS.md](file:///media/francesco/Data/Coding/DeltaClass/ingka-java-code-assignment/QUESTIONS.md).

**Phase 5: Polish & Final Verification (T+16h - T+20h)**
*   [ ] **Refactor**: Check Cyclomatic Complexity.
*   [ ] **Coverage**: Run JaCoCo. Ensure > 80%.
*   [ ] **Docs**: Update README with "Architect's Manifesto" (Rationale).
*   [ ] **Final Push**.

## 5. "Architect’s Manifesto" Preparation
I will document decisions in a specific `ADR` (Architecture Decision Record) section in the README.
*   *Why Quarkus Events?* Decoupling side effects.
*   *Why Validator Interface?* Extensibility (OCP).
*   *Why Modular Monolith?* Simplified ops vs Microservices complexity for this scale.

## 6. Definition of Done (DoD)
*   [ ] Build Success (`mvn clean install`).
*   [ ] All Tests Pass (Green).
*   [ ] Coverage > 80% (Verified by JaCoCo report).
*   [ ] No Critical Sonar/Lint issues (Clean Code).
*   [ ] [Store](file:///media/francesco/Data/Coding/DeltaClass/ingka-java-code-assignment/.DS_Store) legacy updates happen *only* on success.
*   [ ] GitHub Repo link works and contains CI pipeline status.
