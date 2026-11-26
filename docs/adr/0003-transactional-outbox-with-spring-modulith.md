# 0003. Transactional outbox with Spring Modulith in the AsyncAPI generator

Status: accepted, 2026-11-26. Implemented 2026-11-26 before the AFK loop (steps 1-4); the test of step 5 is board task T-19.

## Context

The generated event producer sends to Kafka inside the service method, before the transaction commits. A failed
commit leaves an event for state that doesn't exist; a crash after the commit and before the send loses the event.
Every command that saves and emits (`withEvents` in the ZDL) has this window.

## Decision

Turn on the ZenWave AsyncAPI generator's transactional outbox with Spring Modulith
([docs](https://www.zenwave360.io/zenwave-sdk/plugins/asyncapi-generator/),
[post](https://www.zenwave360.io/posts/TransactionalOutBoxWithAsyncAPIAndSpringModulith/)): the generated producer
publishes a `Message<?>` through `ApplicationEventPublisher`; Spring Modulith stores it in its event publication
registry in the same transaction and externalizes it to Kafka after commit.

1. `pom.xml`, both `zenwave-sdk-maven-plugin` executions of `AsyncAPIGenerator` (`generate-asyncapi`,
   `generate-asyncapi-client`), in `<configOptions>`:
   ```xml
   <transactionalOutbox>modulith</transactionalOutbox>
   ```
2. Dependencies (Spring Boot 4.1: Spring Modulith 2.1.x, Jackson 3):
   - `org.springframework.modulith:spring-modulith-bom` 2.1.3 in `<dependencyManagement>`;
   - `org.springframework.modulith:spring-modulith-starter-jdbc` (publication registry in PostgreSQL, same
     `DataSource` and transaction as JPA; as the reference `zenwave-playground/examples/transactional-outbox-modulith-jpa`.
     Not `-starter-jpa`: its table would come from Hibernate `ddl-auto`, which is `none` outside tests);
   - `io.zenwave360.sdk:spring-modulith-events-scs` 1.3.1 (externalizes `Message<?>` events through Spring Cloud
     Stream, routed by the generated destination header: the binding names `on-fulfillment-<event>-out`);
   - `tools.jackson.dataformat:jackson-dataformat-avro` (Avro payloads in the registry; version from Spring Boot).
3. A `@Configuration` in `config` with `@EnableSpringCloudStreamEventExternalization` (`EventExternalizationConfiguration`).
4. `application.yml`:
   ```yaml
   spring.modulith.events:
     jdbc.schema-initialization.enabled: true   # event_publication table, created on startup (also in tests)
     republish-outstanding-events-on-restart: true
   ```
5. Regenerate, check the generated-once code still compiles (the producer is generated at build time into `target/`;
   `FulfillmentShippingServiceImpl` and the test `InMemory` producer keep the same interface: nothing changed), and a
   test: no event on Kafka when the transaction rolls back, the event arrives after commit (board T-19).

## Consequences

- Delivery becomes at-least-once and after commit. Consumers already tolerate redelivery (the ZFL redelivery rules).
- One more table (`event_publication`) per service database; docker-compose needs nothing new.
- Tests that read events from the in-memory producer keep working; only the Kafka integration tests see the delay.
