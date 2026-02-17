# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**

From an architectural perspective, the core challenge is **granularity vs. overhead**. 
*   **Activity-Based Costing (ABC)**: To allocate labor accurately, we need to correlate worker activity (scanning, picking, packing) with specific Orders, which in turn map to Business Units. This requires a robust event stream from WMS/POS systems.
*   **Shared Resources**: How do we allocate "overhead" (e.g., rent, utilities) or shared transportation (trucks carrying goods for multiple Business Units)? We need configurable **Allocation Keys** (e.g., % of volume, % of weight, or fixed ratio).
*   **Data Freshness**: Do we need real-time cost tracking (stream processing) or is T+1 (batch) sufficient? Real-time allows for immediate operational adjustments but is significantly more complex (CAP theorem trade-offs).
*   **Integration**: Is the source of truth for "Labor" the HR system or the WMS login times? We need a clear System of Record for each cost component.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**

Strategies often fall into **Automation** and **Intelligence**:
1.  **Route Optimization (Transportation)**: Use algorithms (e.g., VRP solvers) to consolidate shipments from Warehouses to Stores, minimizing fuel and driver time.
2.  **Predictive Stocking (Inventory)**: Use ML to predict demand per Store/Region. Positioning stock closer to demand reduces "last mile" or inter-warehouse transfer costs.
3.  **Labor Scheduling**: Optimize shift planning based on predicted order volume to avoid over/under-staffing.
4.  **Batch Picking**: Group orders to minimize picker walking distance in the warehouse.

*Prioritization Framework*: **ROI vs. Implementation Complexity**.
*   *Low Hanging Fruit*: Software-based Route Optimization (High Impact, Med Complexity).
*   *Long Term*: Warehouse Robotics (High Impact, High CapEx).
*   *Implementation*: Start with a "Digital Twin" or simulation to validate savings before physical rollout.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**

Integration transforms "Operational Metrics" into "Financial Impact".
*   **Benefits**: Real-time visibility into P&L per Business Unit/Store. Catching leakage (e.g., excessive overtime or shipping costs) immediately rather than at month-end close.
*   **Architectural Pattern**: **Event-Driven Architecture (EDA)**.
    *   Operational systems (WMS, TMS) emit domain events (`OrderShipped`, `InventoryReceived`).
    *   An **Anti-Corruption Layer (ACL)** translates these into Financial Events (`COGSAccrual`, `LogisticsExpense`).
    *   The ERP/Financial System consumes these validated events.
*   **Data Integrity**: Must ensure **Idempotency** (processing the same event twice doesn't double-charge) and strictly ordered processing where necessary.
*   **Reconciliation**: Automated nightly jobs to compare Operational totals vs. Financial totals to catch drift.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**

*   **Historical Data is Key**: We need a **Data Warehouse** (e.g., Snowflake, BigQuery) separate from the transactional DB to store years of history without impacting performance.
*   **Separation of Concerns**: The operational system handles *execution*; the forecasting system handles *planning*.
*   **Feedback Loops**: The system should track `Actuals vs. Forecast` automatically and alert on significant deviation (Anomaly Detection).
*   **Driver-Based Forecasting**: Don't just "add 5% to last year". Model based on drivers: "Expected Orders" * "Cost per Order" + "Fixed Costs".  If "Cost per Order" rises, investigating *why* (Productivity drop? Fuel price hike?) is easier.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**

*   **Auditability & Compliance**: Financial records (Tax, Audits) often require 7+ years of retention. Deleting the old warehouse data is not an option; it must be **Archived** (Soft Delete or move to Cold Storage).
*   **Benchmarking**: We need the old warehouse's efficiency metrics (Cost per Unit) to validate if the new warehouse is actually performing better (ROI of the move).
*   **Data Continuity**: Even if the *Physical* warehouse changes, the *Logical* Business Unit might remain the same for reporting. The system must handle "Versioning" of the Warehouse entity (e.g., `ZWOLLE-001-v1` vs `ZWOLLE-001-v2`) while aggregating reporting at the Business Unit level.
*   **Cutover Handling**: How do we handle costs incurred *during* the move (moving stock from Old to New)? These are "One-off Transition Costs" and should be tagged separately so they don't skew the operational baseline.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
