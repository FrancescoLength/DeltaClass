# Java Code Assignment

This is a short code assignment that explores various aspects of software development, including API implementation, documentation, persistence layer handling, and testing.

## About the assignment

You will find the tasks of this assignment on [CODE_ASSIGNMENT](CODE_ASSIGNMENT.md) file

## About the code base

This is based on https://github.com/quarkusio/quarkus-quickstarts

### Requirements

To compile and run this demo you will need:

- JDK 17+

In addition, you will need either a PostgreSQL database, or Docker to run one.

### Configuring JDK 17+

Make sure that `JAVA_HOME` environment variables has been set, and that a JDK 17+ `java` command is on the path.

## Building the demo

Execute the Maven build on the root of the project:

```sh
./mvnw package
```

## Running the demo

### Live coding with Quarkus

The Maven Quarkus plugin provides a development mode that supports
live coding. To try this out:

```sh
./mvnw quarkus:dev
```

In this mode you can make changes to the code and have the changes immediately applied, by just refreshing your browser.

    Hot reload works even when modifying your JPA entities.
    Try it! Even the database schema will be updated on the fly.

## (Optional) Run Quarkus in JVM mode

When you're done iterating in developer mode, you can run the application as a conventional jar file.

First compile it:

```sh
./mvnw package
```

Next we need to make sure you have a PostgreSQL instance running (Quarkus automatically starts one for dev and test mode). To set up a PostgreSQL database with Docker:

```sh
docker run -it --rm=true --name quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 15432:5432 postgres:13.3
```

Connection properties for the Agroal datasource are defined in the standard Quarkus configuration file,
`src/main/resources/application.properties`.

Then run it:

```sh
java -jar ./target/quarkus-app/quarkus-run.jar
```
    Have a look at how fast it boots.
    Or measure total native memory consumption...


## See the demo in your browser

Navigate to:

<http://localhost:8080/index.html>

Have fun, and join the team of contributors!

## Troubleshooting

Using **IntelliJ**, in case the generated code is not recognized and you have compilation failures, you may need to add `target/.../jaxrs` folder as "generated sources".

## Architect's Manifesto (Architecture Decision Records)

This section documents the key architectural decisions made during the implementation of the assessment features.

### 1. Modular Monolith with Hexagonal Principles
**Decision**: Adopt a Modular Monolith structure with strict separation of concerns, influenced by Hexagonal Architecture (Ports & Adapters).
**Rationale**: 
- **Scale**: The application is currently monolithic but requires distinct boundaries for `Location`, `Store`, and `Warehouse` domains to facilitate potential future microservices extraction.
- **Maintainability**: Decoupling the Domain (Business Logic) from the Infrastructure (Database, REST Adapters) ensures that changes in frameworks or databases do not ripple through the core logic.

### 2. Transactional Availability for Legacy Systems
**Decision**: Use Quarkus `@Observes(during = TransactionPhase.AFTER_SUCCESS)` for `StoreLegacyUpdateEvent`.
**Rationale**:
- **Consistency**: The requirement stated that legacy updates must *only* occur if the database transaction commits.
- **Decoupling**: Firing an event decouples the `StoreResource` (REST Controller) from the `LegacyStoreManagerGateway` (Infrastructure side-effect). The Resource focuses on the HTTP/DB transaction, while the Event Listener handles the downstream integration safely.

### 3. Strategy Pattern for Validation
**Decision**: Implement `WarehouseValidator` interface with specific implementations (e.g., `WarehouseBusinessValidator`).
**Rationale**:
- **Extensibility (OCP)**: New validation rules (e.g., "Hazmat storage rules") can be added by creating new classes without modifying the core `CreateWarehouseUseCase`.
- **Testability**: Complex validation logic is isolated in pure Java classes that can be unit-tested without framework overhead.

### 4. Contract-First API Design
**Decision**: Leverage OpenAPI for the `Warehouse` domain.
**Rationale**:
- **Governance**: Defining the API contract (YAML) first ensures that the implementation adheres strictly to the agreed-upon specification.
- **Stability**: It prevents "implementation drift" where the code and the documentation diverge over time.