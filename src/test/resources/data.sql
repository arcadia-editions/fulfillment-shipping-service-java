-- Scenario test data. Regenerate with $zenwave-test-scenarios (place-order-flow).
-- DDL derived from entities + modulith data.sql, not exported; validated in step 7 (models: fulfillment-shipping service-all 0.2.0).
--   Only aggregate: Fulfillment (table fulfillment, sequence fulfillment_seq, increment 50; status EnumType.STRING).
-- Fixed test "now" (fixed Clock, still to be added by a board task, T-03): 2026-10-14T10:00:00Z.
-- JSON mirrors: src/test/resources/data/jpa/Fulfillment/<k>.json, k = row position, id = 1 + (k-1)*50.
-- Business ids shared with orders-checkout and catalog-inventory fixtures: ORD-00n, CUST-00n, SKU-00n (same chain ids).
--
-- SCENARIO MATRIX: chain | business ids | Fulfillment state (upstream state implied) | used by
-- C-01 | FUL-001, ORD-001, SKU-001x1 | SCHEDULED, paymentCapturedAt set (order CONFIRMED, flow completed) | outcome completed; scheduleFulfillment redelivery no-op; recordPaymentCaptured redelivery no-op; markReadyToShip allowed; repository tests findById(1)
-- C-02 | ORD-002, SKU-002x2 + SKU-003x1 | no row (order CREATED, payment authorized; OrderConfirmed next) | scheduleFulfillment start (input from orders C-02); multiplicity of the input
-- C-04 | FUL-004, ORD-004, SKU-002x1 | FAILED (payment voided, stock released, order cancel pending) | scheduleFulfillment failed result shape; recordPaymentCaptured / cancelFulfillment no-op on FAILED
-- C-07 | ORD-007 | no row (order CANCELLED PAYMENT_DECLINED, never confirmed) | cancelFulfillment / recordPaymentCaptured no-op without Fulfillment (also C-03, C-05, C-06, C-08)
-- C-09 | FUL-009, ORD-009, SKU-002x1 | CANCELLED PAYMENT_VOIDED, was SCHEDULED (capture failed; order CANCELLED) | outcome cancelled after capture failure; cancelFulfillment redelivery no-op; recordPaymentCaptured no-op on CANCELLED
-- C-10 | FUL-010, ORD-010, SKU-002x1 | FAILED (void refused; order CANCELLED PAYMENT_VOID_FAILED) | outcome cancelled after fulfillment failure, void refused; cancelFulfillment no-op on FAILED
-- C-20 | FUL-020, ORD-020, SKU-002x2 + SKU-003x1 | SCHEDULED, capture pending | recordPaymentCaptured sets paymentCapturedAt; markReadyToShip refused; multiplicity (2 items); off-chain
-- C-21 | FUL-021, ORD-021, SKU-002x1 | SCHEDULED, capture failed (order cancel in progress) | cancelFulfillment from SCHEDULED (PAYMENT_VOIDED); off-chain
-- C-22 | FUL-022, ORD-022, SKU-002x1 | READY_TO_SHIP (captured, carrier assigned; readyAt = capture + 30 s) | cancelFulfillment from READY_TO_SHIP; recordPaymentCaptured no-op not SCHEDULED; markReadyToShip refused (not SCHEDULED); off-chain
--
-- COVERAGE (need -> chain, or why no row)
-- Lifecycle: markReadyToShip from SCHEDULED -> C-01 (captured), C-20 (not captured: refused); cancelFulfillment from SCHEDULED -> C-21, from READY_TO_SHIP -> C-22;
--   scheduleFulfillment has no from: (creates) -> C-02 (no row)
-- Branches: scheduleFulfillment SCHEDULED | FAILED -> no row decides it: stand-in rule of T-06 (ADR 0002, postalCode 00000 fails); result shapes C-01 / C-04;
--   recordPaymentCaptured sets -> C-20, no Fulfillment -> C-07, not SCHEDULED -> C-04 / C-09 / C-22, already set -> C-01;
--   markReadyToShip refused until paymentCapturedAt -> C-20, allowed -> C-01; cancelFulfillment no-op: none C-07, FAILED C-04 / C-10, CANCELLED C-09
-- Outcomes (fulfillment side): completed -> C-01; orderCancelled after capture failure -> C-09 (C-21 before cancelFulfillment); after fulfillment failure -> C-04
--   (cancel pending), void refused -> C-10; cancelled by decline / retries exhausted / expiry -> no row (C-03, C-05..C-08: never confirmed);
--   stockGone -> no row (no order); notificationFailed -> no row (notifications context)
-- Multiplicity: items minlength(1) -> C-20 (2 items); scheduleFulfillment input -> C-02
-- Time: no timer or deadline in this context -> no row
-- Redelivery: scheduleFulfillment for an existing orderId -> C-01; recordPaymentCaptured already set -> C-01; cancelFulfillment on CANCELLED -> C-09
-- Read side: openapi.yml 0.2.0 has no paths -> no row
--
-- ASSUMPTIONS: assumption | value used | open question
-- A-01 fulfillmentId format | FUL-<order number> (fixture ids only) | decided T-05: the service generates a UUIDv7
-- A-02 failureReason of a FAILED fulfillment | CARRIER_UNAVAILABLE | decided T-06, ADR 0002 (stand-in: postalCode 00000 fails)
-- A-03 cancellationReason = OrderCancelled.reason (StockReleaseReason name) | PAYMENT_VOIDED | -
-- A-04 carrierCode / trackingNumber of a READY_TO_SHIP fulfillment | CARRIER-01 / TRK-022 | no input carries them (P-01)
-- A-05 scheduledAt / failedAt = order confirmedAt + 10 s; paymentCapturedAt = scheduledAt + 30 s; FAILED rows have no scheduledAt | as listed | -
-- A-06 cancelledAt = order cancelledAt + 1 s (C-09: order 09:51:39, stock released 09:51:38) | 09:51:40 | -
-- A-07 C-20..C-22 are off-chain: no order, reservation or payment rows (fulfillment never reads other contexts); times 09:55:1n | as listed | renumbered from C-15..C-17 (payments uses C-15..C-19, notifications C-30..)
-- A-08 differences from the modulith fixtures: status is the enum name (EnumType.STRING; modulith stores enum ids 1..4); timestamps follow orders' ORD-00n chain | as listed | validated in step 7

-- Fulfillment Shipping
INSERT INTO fulfillment (id, version, fulfillment_id, order_id, customer_id, status, shipping_address, items, carrier_code, tracking_number, scheduled_at, ready_at, failed_at, failure_reason, payment_captured_at, cancelled_at, cancellation_reason)
VALUES (nextval('fulfillment_seq'), 0, 'FUL-001', 'ORD-001', 'CUST-001', 'SCHEDULED',
        '{"name":"Arcadia Customer 1","line1":"1 Book Lane","line2":null,"city":"Madrid","region":null,"postalCode":"28001","country":"ES"}'::jsonb,
        '[{"sku":"SKU-001","quantity":1}]'::jsonb,
        NULL, NULL, '2026-09-26T13:05:10Z', NULL, NULL, NULL, '2026-09-26T13:05:40Z', NULL, NULL); -- C-01
INSERT INTO fulfillment (id, version, fulfillment_id, order_id, customer_id, status, shipping_address, items, carrier_code, tracking_number, scheduled_at, ready_at, failed_at, failure_reason, payment_captured_at, cancelled_at, cancellation_reason)
VALUES (nextval('fulfillment_seq'), 0, 'FUL-004', 'ORD-004', 'CUST-004', 'FAILED',
        '{"name":"Arcadia Customer 4","line1":"4 Book Lane","line2":null,"city":"Madrid","region":null,"postalCode":"28004","country":"ES"}'::jsonb,
        '[{"sku":"SKU-002","quantity":1}]'::jsonb,
        NULL, NULL, NULL, NULL, '2026-10-14T09:50:44Z', 'CARRIER_UNAVAILABLE', NULL, NULL, NULL); -- C-04
INSERT INTO fulfillment (id, version, fulfillment_id, order_id, customer_id, status, shipping_address, items, carrier_code, tracking_number, scheduled_at, ready_at, failed_at, failure_reason, payment_captured_at, cancelled_at, cancellation_reason)
VALUES (nextval('fulfillment_seq'), 0, 'FUL-009', 'ORD-009', 'CUST-009', 'CANCELLED',
        '{"name":"Arcadia Customer 9","line1":"9 Book Lane","line2":null,"city":"Madrid","region":null,"postalCode":"28009","country":"ES"}'::jsonb,
        '[{"sku":"SKU-002","quantity":1}]'::jsonb,
        NULL, NULL, '2026-10-14T09:50:49Z', NULL, NULL, NULL, NULL, '2026-10-14T09:51:40Z', 'PAYMENT_VOIDED'); -- C-09
INSERT INTO fulfillment (id, version, fulfillment_id, order_id, customer_id, status, shipping_address, items, carrier_code, tracking_number, scheduled_at, ready_at, failed_at, failure_reason, payment_captured_at, cancelled_at, cancellation_reason)
VALUES (nextval('fulfillment_seq'), 0, 'FUL-010', 'ORD-010', 'CUST-010', 'FAILED',
        '{"name":"Arcadia Customer 10","line1":"10 Book Lane","line2":null,"city":"Madrid","region":null,"postalCode":"28010","country":"ES"}'::jsonb,
        '[{"sku":"SKU-002","quantity":1}]'::jsonb,
        NULL, NULL, NULL, NULL, '2026-10-14T09:50:50Z', 'CARRIER_UNAVAILABLE', NULL, NULL, NULL); -- C-10
INSERT INTO fulfillment (id, version, fulfillment_id, order_id, customer_id, status, shipping_address, items, carrier_code, tracking_number, scheduled_at, ready_at, failed_at, failure_reason, payment_captured_at, cancelled_at, cancellation_reason)
VALUES (nextval('fulfillment_seq'), 0, 'FUL-020', 'ORD-020', 'CUST-020', 'SCHEDULED',
        '{"name":"Arcadia Customer 20","line1":"20 Book Lane","line2":null,"city":"Madrid","region":null,"postalCode":"28020","country":"ES"}'::jsonb,
        '[{"sku":"SKU-002","quantity":2},{"sku":"SKU-003","quantity":1}]'::jsonb,
        NULL, NULL, '2026-10-14T09:55:15Z', NULL, NULL, NULL, NULL, NULL, NULL); -- C-20
INSERT INTO fulfillment (id, version, fulfillment_id, order_id, customer_id, status, shipping_address, items, carrier_code, tracking_number, scheduled_at, ready_at, failed_at, failure_reason, payment_captured_at, cancelled_at, cancellation_reason)
VALUES (nextval('fulfillment_seq'), 0, 'FUL-021', 'ORD-021', 'CUST-021', 'SCHEDULED',
        '{"name":"Arcadia Customer 21","line1":"21 Book Lane","line2":null,"city":"Madrid","region":null,"postalCode":"28021","country":"ES"}'::jsonb,
        '[{"sku":"SKU-002","quantity":1}]'::jsonb,
        NULL, NULL, '2026-10-14T09:55:16Z', NULL, NULL, NULL, NULL, NULL, NULL); -- C-21
INSERT INTO fulfillment (id, version, fulfillment_id, order_id, customer_id, status, shipping_address, items, carrier_code, tracking_number, scheduled_at, ready_at, failed_at, failure_reason, payment_captured_at, cancelled_at, cancellation_reason)
VALUES (nextval('fulfillment_seq'), 0, 'FUL-022', 'ORD-022', 'CUST-022', 'READY_TO_SHIP',
        '{"name":"Arcadia Customer 22","line1":"22 Book Lane","line2":null,"city":"Madrid","region":null,"postalCode":"28022","country":"ES"}'::jsonb,
        '[{"sku":"SKU-002","quantity":1}]'::jsonb,
        'CARRIER-01', 'TRK-022', '2026-10-14T09:55:17Z', '2026-10-14T09:56:17Z', NULL, NULL, '2026-10-14T09:55:47Z', NULL, NULL); -- C-22
