# ADR-001: Decoupling Store Legacy System Updates using CDI Events

## Status
Accepted

## Context
In the `monolith` application, the `StoreResource` handles operations for creating and updating `Store` entities. A legacy system requirement mandates that any change to a `Store` must also be synchronized to a legacy system (simulated by writing to a temporary file via `LegacyStoreManagerGateway`).

Previously, this synchronization was performed synchronously within the REST API transaction. This approach had several drawbacks:
1.  **Performance**: The API response time was directly coupled to the legacy system's performance (file I/O).
2.  **Coupling**: The domain logic in `StoreResource` was tightly coupled to the legacy integration details.
3.  **Transaction Scope**: A failure in the legacy system integration could potentially rollback the main database transaction, or conversely, a successful legacy update could occur even if the database transaction failed (if not properly managed).

## Decision
We decided to decouple the legacy system synchronization from the main request processing flow using **CDI (Contexts and Dependency Injection) Events**.

Specifically:
1.  We introduced a custom event class `StoreLegacyUpdateEvent` that carries the state of the store and the type of operation (creation vs. update).
2.  The `StoreResource` publishes this event using `jakarta.enterprise.event.Event<StoreLegacyUpdateEvent>` instead of calling the gateway directly.
3.  We created a listener `StoreLegacyUpdateEventListener` that observes this event.
4.  The observer is configured with `@Observes(during = TransactionPhase.AFTER_SUCCESS)`, ensuring that the legacy update is triggered **only after** the main database transaction has successfully committed.

## Consequences

### Positive
*   **Improved Response Time**: The API client receives a response as soon as the database transaction commits, without waiting for the legacy system operation (although currently synchronous in the same thread, the pattern allows for easy async migration).
*   **Transaction Safety**: The legacy update is strictly conditional on the success of the database transaction. This prevents inconsistencies where the legacy system is updated but the local database is not.
*   **Separation of Concerns**: `StoreResource` focuses on HTTP and core domain orchestration, while the listener handles the side effect of legacy synchronization.
*   **Testability**: The `LegacyStoreManagerGateway` can be mocked or verified independently of the resource logic.

### Negative
*   **Eventual Consistency**: The legacy system is updated slightly after the main database. In a failure scenario (e.g., app crash right after commit), the legacy system might not be updated.
*   **Error Handling Complexity**: If the legacy update fails (in the listener), the API response has already been sent (200 OK or 201 Created). The client is not notified of the legacy sync failure. Robustness would require a background retry mechanism or specific error logging (which we have implemented via Logger).

## Implementation Details
*   **Event**: `com.fulfilment.application.monolith.stores.StoreLegacyUpdateEvent`
*   **Listener**: `com.fulfilment.application.monolith.stores.StoreLegacyUpdateEventListener`
*   **Gateway**: `com.fulfilment.application.monolith.stores.LegacyStoreManagerGateway`
