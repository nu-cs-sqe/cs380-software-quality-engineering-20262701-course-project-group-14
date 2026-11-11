# TurnEngine BVA

## Constructor under test: `TurnEngine(GameState, DiscardPile, ExplodingKittenResolver, Map<CardType, CardEffect>)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TE-01 | Valid turn dependencies exist. | Non-null state, discard pile, resolver, and empty effects map | Creates an engine in `ACTIONS_ALLOWED`. | :white_check_mark: |
| TE-02 | No engine exists. | `state = null`; all other inputs valid | Throws `NullPointerException` with `"Game state must not be null."`; no engine is created. | :white_check_mark: |
| TE-03 | No engine exists. | `discardPile = null`; all other inputs valid | Throws `NullPointerException` with `"Discard pile must not be null."`; no engine is created. | :white_check_mark: |
| TE-04 | No engine exists. | `resolver = null`; all other inputs valid | Throws `NullPointerException` with `"Exploding Kitten resolver must not be null."`; no engine is created. | :white_check_mark: |
| TE-05 | No engine exists. | `effects = null`; all other inputs valid | Throws `NullPointerException` with `"Card effects must not be null."`; no engine is created. | :white_check_mark: |
| TE-06 | No engine exists. | Effects map contains a `null` key | Throws `NullPointerException` with `"Card effect type must not be null."`; no engine is created. | :white_check_mark: |
| TE-07 | No engine exists. | Effects map contains a `null` value | Throws `NullPointerException` with `"Card effect must not be null."`; no engine is created. | :white_check_mark: |
| TE-08 | A caller owns a mutable effects map. | Construct engine, then change the source map | Later source-map changes do not change the engine's registered effects. | :x: |

## Method under test: `draw()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TE-09 | Phase is `ACTIONS_ALLOWED`; deck is empty. | None | Returns `EMPTY_DECK`; hand, discard pile, phase, and current player do not change. | :x: |
| TE-10 | Phase is `ACTIONS_ALLOWED`; deck top is a non-Kitten card; at least two players are alive. | None | Drawn card enters the current player's hand, deck size decreases by one, current player advances exactly once, and result is `NORMAL_CARD_DRAWN` with the card type. | :x: |
| TE-11 | Phase is `AWAITING_DEFUSE_POSITION`. | None | Returns `REJECTED_PENDING_DEFUSE`; no card is drawn and no state changes. | :x: |
| TE-12 | Deck top is an Exploding Kitten; resolver returns `DEFUSE_POSITION_REQUIRED`. | None | Saves the pending Kitten, returns `DEFUSE_POSITION_REQUIRED`, changes phase to `AWAITING_DEFUSE_POSITION`, and does not advance the player. | :x: |
| TE-13 | Deck top is an Exploding Kitten; resolver returns `PLAYER_ELIMINATED`. | None | Returns `PLAYER_ELIMINATED` and advances to the next living player exactly once. | :x: |

## Method under test: `playCard(int handIndex)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TE-14 | Phase is `ACTIONS_ALLOWED`; hand is non-empty. | `handIndex = -1` | Returns `REJECTED` with `INVALID_HAND_INDEX`; state does not change. | :x: |
| TE-15 | Phase is `ACTIONS_ALLOWED`; hand size is `n`. | `handIndex = n` | Returns `REJECTED` with `INVALID_HAND_INDEX`; state does not change. | :x: |
| TE-16 | Phase is `AWAITING_DEFUSE_POSITION`. | Any hand index | Returns `REJECTED` with `DEFUSE_RESOLUTION_REQUIRED`; state does not change. | :x: |
| TE-17 | Selected card has no registered effect. | A valid hand index | Returns `REJECTED` with `CARD_NOT_PLAYABLE`; state does not change. | :x: |
| TE-18 | Selected effect is registered but `canPlay` returns `false`. | A valid hand index | Returns `REJECTED` with `PRECONDITION_NOT_MET`; state does not change. | :x: |
| TE-19 | Selected effect can play and returns `endsTurn = false`. | A valid hand index | Removes the selected card, executes the effect once, discards the played card, preserves revealed-card order, and does not advance the player. | :x: |
| TE-20 | Selected effect can play and returns `endsTurn = true`; at least two players are alive. | A valid hand index | Removes and discards the selected card, executes the effect once, advances exactly once, and returns `TURN_ENDED`. | :x: |
| TE-21 | Engine was built from a mutable effects map that was later changed. | Play a card whose original mapping was removed or replaced in the source map | Uses the mapping captured at construction time. | :x: (planned with TE-08) |

## Method under test: `resolveDefuse(int position)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TE-22 | No Kitten is pending. | Any position | Returns `NOT_AWAITING_DEFUSE`; state does not change. | :x: |
| TE-23 | A Kitten is pending; resolver returns `INVALID_POSITION`. | Invalid position | Returns `INVALID_POSITION`; pending Kitten and current player remain unchanged. | :x: |
| TE-24 | A Kitten is pending; resolver returns `DEFUSE_NOT_FOUND`. | A position in range | Returns `DEFUSE_NOT_FOUND`; pending Kitten and current player remain unchanged. | :x: |
| TE-25 | A Kitten is pending; resolver returns `DEFUSED`; at least two players are alive. | A valid position | Returns `DEFUSED`, clears the pending Kitten, changes phase to `ACTIONS_ALLOWED`, and advances exactly once. | :x: |

## Method under test: `getPhase()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TE-26 | No Kitten is pending. | None | Returns `ACTIONS_ALLOWED`. | implemented in TE-01 |
| TE-27 | A Kitten is pending. | None | Returns `AWAITING_DEFUSE_POSITION`. | :x: (planned in TE-12) |

## Method under test: `getSnapshot()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TE-28 | A valid game is in progress. | None | Returns the current player's name and hand, current deck and discard sizes, living-player count, and current phase without exposing mutable state. | :x: |
