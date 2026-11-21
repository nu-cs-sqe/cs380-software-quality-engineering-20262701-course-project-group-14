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
| TE-08 | A caller owns a mutable effects map. | Construct engine, then change the source map | Later source-map changes do not change the engine's registered effects. | :white_check_mark: |

## Method under test: `draw()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TE-09 | Phase is `ACTIONS_ALLOWED`; deck is empty. | None | Returns `EMPTY_DECK`; hand, discard pile, phase, and current player do not change. | :white_check_mark: |
| TE-10 | Phase is `ACTIONS_ALLOWED`; deck top is a non-Kitten card; at least two players are alive. | None | Drawn card enters the current player's hand, deck size decreases by one, current player advances exactly once, and result is `NORMAL_CARD_DRAWN` with the card type. | :white_check_mark: (turn advancement tested with a GameState stub) |
| TE-11 | Phase is `AWAITING_DEFUSE_POSITION`. | None | Returns `REJECTED_PENDING_DEFUSE`; no card is drawn and no state changes. | :white_check_mark: (waiting state reached with a resolver stub) |
| TE-12 | Deck top is an Exploding Kitten; resolver returns `DEFUSE_POSITION_REQUIRED`. | None | Saves the pending Kitten, returns `DEFUSE_POSITION_REQUIRED`, changes phase to `AWAITING_DEFUSE_POSITION`, and does not advance the player. | :white_check_mark: (tested with a resolver stub) |
| TE-13 | Deck top is an Exploding Kitten; resolver returns `PLAYER_ELIMINATED`. | None | Returns `PLAYER_ELIMINATED` and advances to the next living player exactly once. | :white_check_mark: (tested with resolver and GameState stubs) |

## Method under test: `playCard(int handIndex)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TE-14 | Phase is `ACTIONS_ALLOWED`; hand is non-empty. | `handIndex = -1` | Returns `REJECTED` with `INVALID_HAND_INDEX`; state does not change. | :white_check_mark: |
| TE-15 | Phase is `ACTIONS_ALLOWED`; hand size is `n`. | `handIndex = n` | Returns `REJECTED` with `INVALID_HAND_INDEX`; state does not change. | :white_check_mark: |
| TE-16 | Phase is `AWAITING_DEFUSE_POSITION`. | Any hand index | Returns `REJECTED` with `DEFUSE_RESOLUTION_REQUIRED`; state does not change. | :white_check_mark: (waiting state reached with a resolver stub) |
| TE-17 | Selected card has no registered effect. | A valid hand index | Returns `REJECTED` with `CARD_NOT_PLAYABLE`; state does not change. | :white_check_mark: |
| TE-18 | Selected effect is registered but `canPlay` returns `false`. | A valid hand index | Returns `REJECTED` with `PRECONDITION_NOT_MET`; state does not change. | :white_check_mark: |
| TE-19 | Selected effect can play and returns `endsTurn = false`. | A valid hand index | Removes the selected card, executes the effect once, discards the played card, preserves revealed-card order, and does not advance the player. | :white_check_mark: (tested with effect and discard pile stubs) |
| TE-20 | Selected effect can play and returns `endsTurn = true`; at least two players are alive. | A valid hand index | Removes and discards the selected card, executes the effect once, advances exactly once, and returns `TURN_ENDED`. | :white_check_mark: (tested with effect, discard pile and GameState stubs) |
| TE-21 | Engine was built from a mutable effects map that was later changed. | Play a card whose original mapping was removed or replaced in the source map | Uses the mapping captured at construction time. | implemented in TE-08 |
| TE-29 | Player 1 plays Attack during an ordinary turn. | Valid Attack hand index | Attack is discarded; Player 2 becomes current and receives two turns. After Player 2 completes one normal draw, Player 2 remains current; after the second, Player 1 becomes current. | :white_check_mark: |
| TE-30 | Player 2 has two Attack turns remaining and immediately plays Attack. | Valid Attack hand index | Player 3 becomes current and receives four turns. | :white_check_mark: |
| TE-31 | Player 2 completes one of two Attack turns, then plays Attack. | Valid Attack hand index | Player 3 becomes current and receives three turns. | :white_check_mark: |
| TE-32 | Player 2 has two Attack turns and plays Skip. | Valid Skip hand index | Skip is discarded; Player 2 remains current with one turn remaining. | :x: |
| TE-33 | Player 2 has two Attack turns, draws a Kitten and successfully Defuses it. | Valid Defuse position | Player 2 remains current with one turn remaining. | :x: |
| TE-34 | Player 2 has Attack turns remaining and is eliminated by a Kitten. | Draw | Player 3 becomes current with one ordinary turn; Player 2's remaining Attack debt is cleared. | :x: |

## Method under test: `resolveDefuse(int position)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TE-22 | No Kitten is pending. | Any position | Returns `NOT_AWAITING_DEFUSE`; state does not change. | :white_check_mark: |
| TE-23 | A Kitten is pending; resolver returns `INVALID_POSITION`. | Invalid position | Returns `INVALID_POSITION`; pending Kitten and current player remain unchanged. | :white_check_mark: (tested with a resolver stub) |
| TE-24 | A Kitten is pending; resolver returns `DEFUSE_NOT_FOUND`. | A position in range | Returns `DEFUSE_NOT_FOUND`; pending Kitten and current player remain unchanged. | :white_check_mark: (tested with a resolver stub) |
| TE-25 | A Kitten is pending; resolver returns `DEFUSED`; at least two players are alive. | A valid position | Returns `DEFUSED`, clears the pending Kitten, changes phase to `ACTIONS_ALLOWED`, and advances exactly once. | :white_check_mark: (tested with resolver and GameState stubs) |

## Method under test: `getPhase()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TE-26 | No Kitten is pending. | None | Returns `ACTIONS_ALLOWED`. | implemented in TE-01 |
| TE-27 | A Kitten is pending. | None | Returns `AWAITING_DEFUSE_POSITION`. | implemented in TE-12 |

## Method under test: `getSnapshot()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TE-28 | A valid game is in progress. | None | Returns the current player's name and hand, current deck and discard sizes, living-player count, and current phase without exposing mutable state. | :white_check_mark: (tested through TurnActions with dependency stubs) |

## Integration coverage

### F2-01 — Normal draw

- Method under test: `TurnEngine.draw()`
- Implemented: :white_check_mark:

Initial state:
- Two living players each hold 8 cards, Player 1 is current.
- Deck order is [SKIP, SHUFFLE], with SKIP on top.
- Discard pile is empty, phase is ACTIONS_ALLOWED.

Action:
- Call draw() once.

Expected result:
- Returns NORMAL_CARD_DRAWN with type SKIP.
- Player 1 receives the drawn card and holds 9 cards.
- Player 2's hand stays unchanged and Player 2 becomes current.
- Only SHUFFLE remains in the deck.
- Discard pile stays empty, phase remains ACTIONS_ALLOWED.

### F2-02 — Skip ends the turn

- Method under test: TurnEngine.playCard(int)
- Implemented: :white_check_mark:

Initial state:
- Two living players each hold 8 cards, Player 1 is current.
- Player 1 holds SKIP at index 0, and SkipEffect is registered.
- Deck order is [ATTACK, SHUFFLE], discard pile is empty.
- Phase is ACTIONS_ALLOWED.

Action:
- Call playCard(0) once.

Expected result:
- Returns TURN_ENDED with failure NONE.
- Player 1 retains the other 7 cards.
- The discard pile receives the played SKIP.
- Player 2's hand stays unchanged and Player 2 becomes current.
- Deck order and size stay unchanged.
- Phase remains ACTIONS_ALLOWED.

### F2-03 — Consecutive ordinary turns (IT-05)

- Methods under test: TurnEngine.draw(), getSnapshot().
- State: Two living players each hold one Defuse; Player 1 is current;
  deck order is [ATTACK, SHUFFLE]; shared discard pile is empty.
- Action: Draw once as Player 1, then once as Player 2.
- Expected: Current player changes 1 -> 2 -> 1; each hand grows to two;
  each player receives the expected card; deck and discard sizes are zero;
  phase remains ACTIONS_ALLOWED.
- Implemented: :white_check_mark:

### F2-04 — Empty-deck integration (IT-02)

- Methods under test: TurnEngine.draw(), getSnapshot().
- State: Two living players each hold one Defuse; Player 1 is current;
  deck and discard pile are empty; phase is ACTIONS_ALLOWED.
- Action: Attempt to draw.
- Expected: EMPTY_DECK with no drawn card; both hands, current player,
  deck, discard pile and phase remain unchanged.
- Implemented: :white_check_mark:
