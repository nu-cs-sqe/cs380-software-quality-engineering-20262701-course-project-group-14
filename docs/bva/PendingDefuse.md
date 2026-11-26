# PendingDefuse BVA

`PendingDefuse` is the `PendingAction` that `TurnEngine` stores in its single
pending-action slot while the current player must choose where to reinsert a
drawn Exploding Kitten. It only holds the Kitten and reports its phase; the
engine still validates positions, calls the resolver, and completes the turn.

## Method under test: `PendingDefuse(Card kitten)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PD-01 | No pending action exists. | An `EXPLODING_KITTEN` card | Creates a pending Defuse that keeps the same Kitten instance. | :white_check_mark: |
| PD-02 | No pending action exists. | `kitten = null` | Throws `NullPointerException` with `"Pending Kitten must not be null."`; no pending action is created. | :white_check_mark: |
| PD-03 | No pending action exists. | A `DEFUSE` card | Throws `IllegalArgumentException` with `"Pending Defuse requires an Exploding Kitten."`; no pending action is created. | :white_check_mark: |

## Method under test: `getPhase()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PD-04 | A pending Defuse holds an Exploding Kitten. | None | Returns `AWAITING_DEFUSE_POSITION`. | :white_check_mark: |

## Getter coverage

| Public method | Covered by |
|---|---|
| `getKitten()` | PD-01 |
