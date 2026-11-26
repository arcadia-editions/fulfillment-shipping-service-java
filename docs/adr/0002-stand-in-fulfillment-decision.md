# 0002. Stand-in fulfillment decision

Status: accepted, 2026-11-26 (demo: simplest rule that runs; board T-06)

## Context

`scheduleFulfillment` answers SCHEDULED or FAILED (place-order-flow.zfl:178-188), but the models do not say which
system decides, nor the failure reasons. There is no warehouse or carrier to call in this demo.

## Decision

Same as the modulith (`StandInFulfillmentDecision`): an outbound port `FulfillmentDecision` in `core.outbound` with
one deterministic stand-in adapter.

- shipping address `postalCode` `00000`: FAILED, `failureReason` `CARRIER_UNAVAILABLE` (data.sql A-02);
- any other address: SCHEDULED.

Flow tests that need FAILED send an OrderConfirmed with postal code `00000`. No other failure reasons.

### Ready to ship (added 2026-11-26, P-01)

There is no warehouse either: `markReadyToShip(id)` has no inbound adapter and its input carries no carrier data. The
service sets the stand-in values when it moves SCHEDULED to READY_TO_SHIP: `carrierCode` `STAND-IN`,
`trackingNumber` `TRK-` + fulfillmentId. No port: a real carrier assignment would add one.

## Consequences

No real warehouse integration; replacing it is a new adapter behind the same port.
