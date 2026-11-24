# TurnEngineFactory BVA

## Method under test: `create(GameState state)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TEF-01 | No engine exists. | `state = null` | Throws `NullPointerException` with `"Game state must not be null."`; no engine is created. | :white_check_mark: |
| TEF-02 | Two active players exist; Player 1 holds Attack. | The valid game state | Returns turn actions in which playing Attack succeeds, discards Attack, and passes control to Player 2 with Attack turn debt. | :white_check_mark: |
| TEF-03 | Two active players exist; Player 1 holds Skip. | The valid game state | Returns turn actions in which playing Skip succeeds, discards Skip, and passes control to Player 2. | :white_check_mark: |

The factory owns one discard pile and one resolver per game. The same discard
pile is supplied to both the resolver and `TurnEngine`. Registration tests use
the returned `TurnActions` interface and observable state rather than inspecting
the internal effect map.

## Method under test: `create(GameState state, RandomSource random)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TEF-04 | Two active players exist and the current player holds a matching Cat Card pair. | Valid game state and a fixed selection source returning `1` | Creates an engine whose pair play transfers the target's second card, keeps the current turn, and discards the pair. W9-IT05 covers each Cat Card type. | :white_check_mark: |
| TEF-05 | No engine exists. | `state = null`; valid selection source | Throws `NullPointerException` with `"Game state must not be null."`. | :x: |
| TEF-06 | No engine exists. | A valid game state; `random = null` | Throws `NullPointerException` with `"Random source must not be null."`. | :x: |
