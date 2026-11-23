# TurnEngineFactory BVA

## Method under test: `create(GameState state)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| TEF-01 | No engine exists. | `state = null` | Throws `NullPointerException` with `"Game state must not be null."`; no engine is created. | :white_check_mark: |
| TEF-02 | Two active players exist; Player 1 holds Attack. | The valid game state | Returns turn actions in which playing Attack succeeds, discards Attack, and passes control to Player 2 with Attack turn debt. | :white_check_mark: |
| TEF-03 | Two active players exist; Player 1 holds Skip. | The valid game state | Returns turn actions in which playing Skip succeeds, discards Skip, and passes control to Player 2. | :x: |

The factory owns one discard pile and one resolver per game. The same discard
pile is supplied to both the resolver and `TurnEngine`. Registration tests use
the returned `TurnActions` interface and observable state rather than inspecting
the internal effect map.
