# place-order-flow: projections review (gate item 5)

Reviewed 2026-11-26 by claude on behalf of the person, against `../fulfillment-shipping-api/domain-model.zdl` and
`place-order-flow.zfl` (service-all 0.2.0, working copy = package: overlay staleness check prints nothing).

## State machine (from the ZDL, zdl:55, :132, :143, :148)

```
(none) --scheduleFulfillment[SCHEDULED]--> SCHEDULED --markReadyToShip (paymentCapturedAt set)--> READY_TO_SHIP
(none) --scheduleFulfillment[FAILED]-----> FAILED (terminal)
SCHEDULED | READY_TO_SHIP --cancelFulfillment--> CANCELLED (terminal)
recordPaymentCaptured: SCHEDULED, sets paymentCapturedAt, no transition
```

Agrees with the ZFL order (zfl:184-221, :311-320). `FulfillmentTransitions` (generated) has the same `from` sets.

## Sequence per branch (fulfillment side)

| Branch (end) | Sequence | Fixtures |
|---|---|---|
| completed | `OrderConfirmed` -> scheduleFulfillment SCHEDULED -> `FulfillmentScheduled` (after commit, ADR 0003) -> payments capture -> `PaymentCaptured` -> recordPaymentCaptured (no event) | C-02 input, C-01 result, C-20 |
| orderCancelled, capture failed | `FulfillmentScheduled` -> `PaymentCaptureFailed` -> void -> release -> `OrderCancelled` PAYMENT_VOIDED (or PAYMENT_VOID_FAILED) -> cancelFulfillment CANCELLED -> `FulfillmentCancelled` | C-21 before, C-09 after |
| orderCancelled, fulfillment failed | `OrderConfirmed` (postalCode 00000) -> scheduleFulfillment FAILED -> `FulfillmentFailed` -> void (or void refused) -> release -> `OrderCancelled` -> cancelFulfillment no-op | C-23 input, C-04, C-10 |
| orderCancelled before confirmation (decline, retries exhausted, expiry, confirmation rejected) | `OrderCancelled` with no Fulfillment -> no-op | C-07 |
| stockGone, notificationFailed | never reach this context (or leave it unchanged) | none |
| warehouse (outside the flow) | markReadyToShip -> READY_TO_SHIP -> `FulfillmentReadyToShip`; refused until capture | C-01, C-20, C-22 |

## Contracts

| Projection | Check | Result |
|---|---|---|
| `asyncapi.yml` | 4 `send` operations, channels and topics = ZDL `@asyncapi` (zdl:152, :160, :169, :180); Avro payloads in `avro/*.avsc` match the event fields; `Instant` as ISO string; CloudEvents/Kafka common headers trait | ✅ |
| `asyncapi-client.yml` | 3 `receive` operations = the ZDL listeners (zdl:131, :139, :147); `$ref` to producers' `asyncapi.yml` on `main` (residual coupling, recorded in the overlay); retry 5s/30s/5m + DLQ | ✅ |
| `openapi.yml` | `paths: {}`: `@rest("/fulfillments")` has no endpoint (no read side, no markReadyToShip) | ✅ as decided (P-01 rejected for the demo, F-09 of the grill) |
| Generated producer | binding names `on-fulfillment-scheduled-out`, `-failed-out`, `-ready-to-ship-out`, `-cancelled-out`, published through Spring Modulith (ADR 0003); T-07 binds them to the topics above | ✅ |
| `SUMMARY.md` | lists Fulfillment, Scheduled, Failed, ReadyToShip; omits cancellation (`CANCELLED`, `cancelFulfillment`, `FulfillmentCancelled`) and the PaymentCaptured gate | ⚠ docs drift, follow-up below (not a model gap: no proposal) |
| `EVENT_CATALOG.md` | business prose; "scheduling through readiness or an operational exception": same omission of cancellation | ⚠ docs drift, follow-up below (not a model gap: no proposal) |

## P-01 (open at the start)

Decided without a model change (simplest demo answer, as the modulith): `markReadyToShip(id)` stays a service
command with no inbound adapter; `carrierCode` = `STAND-IN`, `trackingNumber` = `TRK-` + fulfillmentId, set by the
service (ADR 0002, "Ready to ship"). P-01 `rejected` with why; T-18 released (`blocked: P-01` removed).

## Follow-up for the models repository (docs only, blocks nothing)

`SUMMARY.md` (lines 14-25) and `EVENT_CATALOG.md` (line 5) do not mention cancellation (`cancelFulfillment`,
CANCELLED, `FulfillmentCancelled`, zdl:146-149, :179-187) nor the PaymentCaptured gate of markReadyToShip
(zdl:135-144). The ZDL and the generated contracts are right, so this is not a model gap and needs no proposal:
add a scope line and the three elements to SUMMARY.md, and one sentence to EVENT_CATALOG.md, in the next change of
`fulfillment-shipping-api` (no re-release of service-all needed).
