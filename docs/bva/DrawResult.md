# DrawResult BVA

## Method under test: `normal(CardType drawnCardType)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| DR-01 | No result exists. | `CardType.ATTACK` | Returns a result whose status is `NORMAL_CARD_DRAWN` and whose drawn-card type is `Optional.of(CardType.ATTACK)`. | :white_check_mark: |
| DR-02 | No result exists. | `null` | Throws `NullPointerException`; no result is created. | :x: |

## Method under test: `defuseRequired()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| DR-03 | No result exists. | None | Returns a result whose status is `DEFUSE_POSITION_REQUIRED` and whose drawn-card type is `Optional.empty()`. | :x: |

## Method under test: `playerEliminated()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| DR-04 | No result exists. | None | Returns a result whose status is `PLAYER_ELIMINATED` and whose drawn-card type is `Optional.empty()`. | :x: |

## Method under test: `emptyDeck()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| DR-05 | No result exists. | None | Returns a result whose status is `EMPTY_DECK` and whose drawn-card type is `Optional.empty()`. | :x: |

## Method under test: `rejectedPendingDefuse()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| DR-06 | No result exists. | None | Returns a result whose status is `REJECTED_PENDING_DEFUSE` and whose drawn-card type is `Optional.empty()`. | :x: |

## Method under test: `getStatus()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| DR-07 | A result was created by one of the public factory methods. | None | Returns the status specified by that factory method and never returns `null`. | :white_check_mark: (implemented in DR-01) |

## Method under test: `getDrawnCardType()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| DR-08 | A normal-card result exists. | None | Returns an `Optional` containing the drawn card type. | :white_check_mark: (implemented in DR-01) |
| DR-09 | A result without a normal drawn card exists. | None | Returns `Optional.empty()` and never returns `null`. | :x: (planned in DR-03 through DR-06) |
