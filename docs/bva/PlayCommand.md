# PlayCommand BVA

`PlayCommand` validates only the non-negative shape of a selection. It does
not inspect a hand or game state. `TurnEngine` is responsible for bounds,
distinct pair indices, matching card types, and legal targets.

## Method under test: `single(int handIndex)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PC-01 | No command exists. | `handIndex = 0` | Creates a command with first index `0`, no second index, and no target. | :white_check_mark: |
| PC-02 | No command exists. | `handIndex = 4` | Creates a command preserving first index `4`, with no second index and no target. | :x: |
| PC-03 | No command exists. | `handIndex = -1` | Throws `IllegalArgumentException` with `"Hand index must not be negative."`; no command is created. | :x: |

## Method under test: `targeted(int handIndex, int targetPlayerIndex)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PC-04 | No command exists. | `handIndex = 0`, `targetPlayerIndex = 0` | Creates a command with first index `0`, no second index, and target seat `0`. | :x: |
| PC-05 | No command exists. | `handIndex = 3`, `targetPlayerIndex = 2` | Creates a command preserving first index `3` and target seat `2`, with no second index. | :x: |
| PC-06 | No command exists. | `handIndex = -1`, `targetPlayerIndex = 0` | Throws `IllegalArgumentException` with `"Hand index must not be negative."`; no command is created. | :x: |
| PC-07 | No command exists. | `handIndex = 0`, `targetPlayerIndex = -1` | Throws `IllegalArgumentException` with `"Target player index must not be negative."`; no command is created. | :x: |

## Method under test: `pair(int firstHandIndex, int secondHandIndex, int targetPlayerIndex)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PC-08 | No command exists. | `firstHandIndex = 0`, `secondHandIndex = 1`, `targetPlayerIndex = 2` | Creates a command preserving both hand indices and target seat `2`. | :x: |
| PC-09 | No command exists. | `firstHandIndex = 4`, `secondHandIndex = 1`, `targetPlayerIndex = 0` | Preserves the original index order; removal order is a later engine responsibility. | :x: |
| PC-10 | No command exists. | `firstHandIndex = 1`, `secondHandIndex = 1`, `targetPlayerIndex = 0` | Creates the command; the engine later rejects the duplicate selection atomically. | :x: |
| PC-11 | No command exists. | `firstHandIndex = -1`, `secondHandIndex = 0`, `targetPlayerIndex = 0` | Throws `IllegalArgumentException` with `"Hand index must not be negative."`; no command is created. | :x: |
| PC-12 | No command exists. | `firstHandIndex = 0`, `secondHandIndex = -1`, `targetPlayerIndex = 0` | Throws `IllegalArgumentException` with `"Second hand index must not be negative."`; no command is created. | :x: |
| PC-13 | No command exists. | `firstHandIndex = 0`, `secondHandIndex = 1`, `targetPlayerIndex = -1` | Throws `IllegalArgumentException` with `"Target player index must not be negative."`; no command is created. | :x: |

## Getter coverage

| Public method | Covered by |
|---|---|
| `getFirstHandIndex()` | PC-01, PC-02, PC-04, PC-05, PC-08, PC-09, PC-10 |
| `getSecondHandIndex()` | PC-01, PC-02, PC-04, PC-05, PC-08, PC-09, PC-10 |
| `getTargetPlayerIndex()` | PC-01, PC-02, PC-04, PC-05, PC-08, PC-09, PC-10 |
