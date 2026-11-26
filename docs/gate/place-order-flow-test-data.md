# place-order-flow: test data review (gate item 13)

Reviewed 2026-11-26 by claude on behalf of the person: the header of `src/test/resources/data.sql` (scenario matrix,
coverage, assumptions) and `src/test/resources/data/jpa/Fulfillment/<k>.json` against the board `docs/tasks.md`.

## Board -> chains

Every chain a task names is in the header (C-01, C-02, C-04, C-07, C-09, C-10, C-20, C-21, C-22, C-23); every row
has its JSON mirror (1.json..7.json, id = 1 + (k-1)*50). Every AFK task has `test data:` (T-03 now names the fixed
test "now").

## Inconsistencies found and fixed

| # | Finding | Fix |
|---|---|---|
| D-01 | C-04 / C-10 are FAILED `CARRIER_UNAVAILABLE` with postal codes `28004` / `28010`; under ADR 0002 those addresses SCHEDULE. | Both rows (data.sql and 2.json / 4.json) now have `postalCode` `00000`. A-09 records that orders' fixtures of the same chains keep `28004` / `28010` (fulfillment never reads them). |
| D-02 | No input for the FAILED branch: T-16 / T-17 had only result shapes, and an OrderConfirmed for ORD-004 / ORD-010 would hit the existing row (redelivery no-op), not the rule. | New input-only chain C-23 (no row): `ORD-023`, `CUST-023`, `SKU-002`x1, `postalCode` `00000` -> FAILED `CARRIER_UNAVAILABLE`. T-16 / T-17 name it. Off-chain like C-20..C-22. |
| D-03 | C-22 READY_TO_SHIP had invented carrier values `CARRIER-01` / `TRK-022` (A-04, P-01). | P-01 rejected with stand-in values (ADR 0002): C-22 (data.sql and 7.json) now `STAND-IN` / `TRK-FUL-022`. |
| D-04 | T-03 (Clock) had no `test data:`. | `test data: data.sql header (fixed test "now" 2026-10-14T10:00:00Z)`. |
| D-05 | The outbox (ADR 0003) has no task to test it. | T-19 (S1), input C-02. |

## Checked, no change

- Lifecycle, branches, outcomes, multiplicity, redelivery rows of the coverage matrix match the board's `test data:`
  (T-08 C-01/C-02, T-10 C-20/C-07/C-04/C-09/C-22/C-01, T-13 C-21/C-22/C-07/C-04/C-10/C-09, T-18 C-01/C-20/C-22).
- Timestamps follow A-05 / A-06; C-22 readyAt = capture + 30 s.
- The data loads: full suite of 2026-11-26 (gate item 8), no `ScriptStatementFailedException` nor
  `Error reading json test data`.
