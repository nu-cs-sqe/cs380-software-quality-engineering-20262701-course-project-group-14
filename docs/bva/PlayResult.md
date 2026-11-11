# PlayResult BVA

## Method under test: `continueTurn(List<CardType> revealedCards)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PR-01 | No result exists. | `[]` | Returns a result with status `CONTINUE_TURN`, failure `NONE`, and an empty revealed-card list. | :white_check_mark: |
| PR-02 | No result exists. | `[SKIP, FAVOR, SHUFFLE]` | Returns a result with status `CONTINUE_TURN`, failure `NONE`, and the three revealed card types in the same order. | :white_check_mark: |
| PR-03 | No result exists. | `null` | Throws `NullPointerException` with message `"Revealed cards must not be null."`; no result is created. | :white_check_mark: |
| PR-04 | No result exists. | A list containing `null` | Throws `NullPointerException` with message `"Revealed cards must not contain null."`; no result is created. | :white_check_mark: |
| PR-05 | A caller owns a mutable source list. | A mutable list containing `[SKIP, FAVOR, SHUFFLE]` | Later changes to the source list do not change the result's revealed cards. | :white_check_mark: |

## Method under test: `turnEnded()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PR-06 | No result exists. | None | Returns a result with status `TURN_ENDED`, failure `NONE`, and an empty revealed-card list. | :white_check_mark: |

## Method under test: `rejected(PlayFailure failure)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PR-07 | No result exists. | `INVALID_HAND_INDEX` | Returns a result with status `REJECTED`, failure `INVALID_HAND_INDEX`, and an empty revealed-card list. | :x: |
| PR-08 | No result exists. | `null` | Throws `NullPointerException`; no result is created. | :x: |
| PR-09 | No result exists. | `NONE` | Throws `IllegalArgumentException`; a rejected result must contain an actual failure reason. | :x: |

## Method under test: `getStatus()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PR-10 | A result was created by a public factory method. | None | Returns the status specified by that factory method and never returns `null`. | :x: (planned in PR-01, PR-02, PR-06, and PR-07) |

## Method under test: `getFailure()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PR-11 | A continuing or turn-ended result exists. | None | Returns `PlayFailure.NONE`. | :white_check_mark: (implemented in PR-01, PR-02, and PR-06) |
| PR-12 | A rejected result exists. | None | Returns the failure supplied to `rejected(...)` and never returns `null`. | :x: (planned in PR-07) |

## Method under test: `getRevealedCards()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PR-13 | A continuing result with revealed cards exists. | None | Returns the revealed card types in their original order. | :white_check_mark: (implemented in PR-02) |
| PR-14 | A result without revealed cards exists. | None | Returns an empty list and never returns `null`. | :x: (planned in PR-01, PR-06, and PR-07) |
| PR-15 | A result with revealed cards exists. | Attempt to modify the returned list. | The modification is rejected with `UnsupportedOperationException`; the result remains unchanged. | :x: |
