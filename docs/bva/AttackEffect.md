# AttackEffect BVA

## Method under test: `canPlay(Player player, Deck deck)`

| ID | State | Input | Expected output | Implemented? |
|---|---|---|---|---|
| AE-01 | A valid player and deck exist. | Non-null player and deck | Returns `true`; neither object changes. | :white_check_mark: |
| AE-02 | A deck exists. | `player = null` | Throws `NullPointerException` with `"Player must not be null."`. | :white_check_mark: |
| AE-03 | A player exists. | `deck = null` | Throws `NullPointerException` with `"Deck must not be null."`. | :x: |

## Method under test: `play(Player player, Deck deck)`

| ID | State | Input | Expected output | Implemented? |
|---|---|---|---|---|
| AE-04 | A valid player and deck exist. | Non-null player and deck | Returns an outcome with `ATTACK_NEXT_PLAYER`, ends the turn and reveals no cards. | :x: |
| AE-05 | A deck exists. | `player = null` | Throws `NullPointerException` with `"Player must not be null."`. | :x: |
| AE-06 | A player exists. | `deck = null` | Throws `NullPointerException` with `"Deck must not be null."`. | :x: |