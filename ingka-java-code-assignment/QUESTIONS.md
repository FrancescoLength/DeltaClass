# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Yes, I would refactor for consistency and separation of concerns.

Current State:
The codebase mixes "Active Record" style (PanacheEntity) in some places with "Repository" style in others. This inconsistency increases cognitive load for developers.

Recommended Strategy: Repository Pattern with Interface-based Ports (Hexagonal Architecture).
Why?
1.  **Decoupling**: The 'Domain' layer should not know about 'Panache' or 'Hibernate'. Implementing a pure interface (e.g., `WarehouseStore`) allows us to swap the persistence implementation (e.g., to an in-memory adapter for high-speed unit tests) without changing business logic.
2.  **Testability**: Mocking a Repository interface is cleaner and less error-prone than mocking static Panache methods.
3.  **Flexibility**: If we need to move to a NoSQL DB or call an external service for some data, the Repository pattern hides this detail from the core application.

Refactoring Plan:
- Define domain interfaces (Ports) for all data access.
- Implement these interfaces in the Infrastructure layer using Panache Repositories.
- Remove direct `Entity.persist()` calls from Resources/Controllers.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
My Choice: Contract-First (OpenAPI -> Code Generation) for all external-facing APIs.

Comparison:

**Contract-First (Warehouse Approach)**
- **Pros**:
  - **Parallel Development**: Frontend and Backend teams can work simultaneously once the YAML is agreed upon.
  - **Single Source of Truth**: The YAML *is* the documentation. No drift between code and docs.
  - **Governance**: APIs can be linted/reviewed for standards (naming conventions, security schemas) before a single line of code is written.
- **Cons**:
  - Initial setup is heavier (build plugins, mapping logic).
  - Can feel restrictive during rapid prototyping phases.

**Code-First (Product/Store Approach)**
- **Pros**:
  - Fastest for "hackathons" or solo developers.
  - Implementation drive the spec, so it "always works" as coded.
- **Cons**:
  - **Documentation Drift**: Generated docs often lag behind or miss nuance.
  - **Breaking Changes**: Easier to accidentally break clients because there's no explicit "contract" review step.

Conclusion: For an enterprise context like this, Contract-First is essential for stability and developer experience across teams.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
Strategy: The Testing Pyramid (Unit > Integration > E2E).

1.  **Prioritization (Unit Tests - 70%)**:
    - **Focus**: Pure Domain Logic (Validators, Use Cases).
    - **Why**: Fastest to write, fastest to run (<1ms), cheapest to maintain. Validating that `CapacityValidator` rejects a stock overflow should be a unit test, not an integration test.
    - **Method**: Use JUnit + Mockito. No Spring/Quarkus context loading.

2.  **Validation (Integration Tests - 20%)**:
    - **Focus**: The "Edges" of the Hexagon (Controllers, Repositories).
    - **Why**: Verify that the JSON marshaling works, the DB queries are correct, and the wiring is successful.
    - **Method**: `@QuarkusTest` with Testcontainers (PostgreSQL).
    - **Critical**: Test the "Happy Path" and 1 "Error Path" per endpoint here. Don't test every permutation.

3.  **Sanity (E2E/Contract Tests - 10%)**:
    - **Focus**: Critical Business Flows (e.g., "Create Store -> Create Warehouse -> Product Shipped").
    - **Why**: Ensure the pieces work together.

**Maintenance**:
- **CI Gates**: Build fails if Coverage < 80%.
- **Mutation Testing**: Use PITest to ensure tests actually fail when code is broken (quality of tests > quantity of tests).
- **Refactoring**: Treat test code as production code. Refactor it to be readable.
```