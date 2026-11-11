# EffectOutcome BVA

## Method under test: `EffectOutcome(boolean endsTurn, List<CardType> revealedCards)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| EO-01 | No outcome exists. | `endsTurn = false`, `revealedCards = []` | Creates an outcome that continues the turn and contains no revealed cards. | :white_check_mark: |
| EO-02 | No outcome exists. | `endsTurn = false`, `revealedCards = [SKIP, FAVOR, SHUFFLE]` | Creates an outcome that continues the turn and contains the three revealed card types in the same order. | :white_check_mark: |
| EO-03 | No outcome exists. | `endsTurn = true`, `revealedCards = []` | Creates an outcome that ends the turn and contains no revealed cards. | :x: |
| EO-04 | No outcome exists. | `revealedCards = null` | Throws `NullPointerException`; no outcome is created. | :x: |
| EO-05 | No outcome exists. | `revealedCards` contains `null` | Throws `NullPointerException`; no outcome is created. | :x: |
| EO-06 | A caller owns a mutable source list. | A mutable list containing `[SKIP, FAVOR, SHUFFLE]` | Later changes to the source list do not change the outcome's revealed cards. | :x: |

## Method under test: `endsTurn()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| EO-07 | An outcome was created with `endsTurn = false`. | None | Returns `false`. | implemented in EO-01 |
| EO-08 | An outcome was created with `endsTurn = true`. | None | Returns `true`. | :x: (planned in EO-03) |

## Method under test: `getRevealedCards()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| EO-09 | An outcome with revealed cards exists. | None | Returns the revealed card types in their original order. | implemented in EO-02 |
| EO-10 | An outcome without revealed cards exists. | None | Returns an empty list and never returns `null`. | implemented in EO-01 |
| EO-11 | An outcome with revealed cards exists. | Attempt to modify the returned list. | The modification is rejected with `UnsupportedOperationException`; the outcome remains unchanged. | :x: |
