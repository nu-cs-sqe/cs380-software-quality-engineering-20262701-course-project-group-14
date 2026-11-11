# SkipEffect BVA

## Method under test: `canPlay(Player, Deck)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| SE-01 | A player and deck exist. | Valid player and deck | Returns `true`; player and deck remain unchanged. | :white_check_mark: |
| SE-02 | A deck exists. | `player = null` | Throws `NullPointerException` with `"Player must not be null."`; deck remains unchanged. | :white_check_mark: |
| SE-03 | A player exists. | `deck = null` | Throws `NullPointerException` with `"Deck must not be null."`; player remains unchanged. | :white_check_mark: |

## Method under test: `play(Player, Deck)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| SE-04 | A player and deck exist. | Valid player and deck | Returns an outcome that ends the turn and reveals no cards; player and deck remain unchanged. | :white_check_mark: |
| SE-05 | A deck exists. | `player = null` | Throws `NullPointerException` with `"Player must not be null."`; deck remains unchanged. | :x: |
| SE-06 | A player exists. | `deck = null` | Throws `NullPointerException` with `"Deck must not be null."`; player remains unchanged. | :x: |
