# GameState BVA

### Method under test: `GameState(List<Player> players, Deck deck)`

| Test Case                                                     | State of the System: `players`                       | State of the System: `deck` | Expected output                                                                                                                                | Implemented?       |
|---------------------------------------------------------------|------------------------------------------------------|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|--------------------|
| GS-01: Create a game state with the minimum number of players | `[Player 1, Player 2]`                               | a valid deck                | `getPlayers()` is `[Player 1, Player 2]`; `getCurrentPlayer()` is Player 1; `getDeck()` is the same deck object                                | :white_check_mark: |
| GS-02: Create a game state with the maximum number of players | `[Player 1, Player 2, Player 3, Player 4, Player 5]` | a valid deck                | `getPlayers()` is `[Player 1, Player 2, Player 3, Player 4, Player 5]` (first is Player 1, last is Player 5); `getCurrentPlayer()` is Player 1 | :white_check_mark: |
| GS-03: Reject a null player list                              | `null`                                               | a valid deck                | Throws `NullPointerException`, "Players must not be null."                                                                                     | :white_check_mark: |
| GS-04: Reject an empty player list                            | `[]`                                                 | a valid deck                | Throws `IllegalArgumentException`, "Players must not be empty."                                                                                | :white_check_mark: |
| GS-05: Reject a null deck                                     | `[Player 1, Player 2]`                               | `null`                      | Throws `NullPointerException`, "Deck must not be null."                                                                                        | :white_check_mark: |

### Method under test: `getPlayers()`

| Test Case                                 | State of the System: `players`                       | Expected output                                      | Implemented?         |
|-------------------------------------------|------------------------------------------------------|------------------------------------------------------|----------------------|
| GS-06: Get the players of a 2-player game | `[Player 1, Player 2]`                               | `[Player 1, Player 2]`                               | implemented in GS-01 |
| GS-07: Get the players of a 5-player game | `[Player 1, Player 2, Player 3, Player 4, Player 5]` | `[Player 1, Player 2, Player 3, Player 4, Player 5]` | implemented in GS-02 |

### Method under test: `getDeck()`

| Test Case           | State of the System: `deck` | Expected output      | Implemented?         |
|---------------------|-----------------------------|----------------------|----------------------|
| GS-08: Get the deck | a valid deck                | the same deck object | implemented in GS-01 |

### Method under test: `getCurrentPlayer()`

| Test Case                                        | State of the System: `players`                       | Expected output | Implemented?         |
|--------------------------------------------------|------------------------------------------------------|-----------------|----------------------|
| GS-09: Get the current player of a 2-player game | `[Player 1, Player 2]`                               | Player 1        | implemented in GS-01 |
| GS-10: Get the current player of a 5-player game | `[Player 1, Player 2, Player 3, Player 4, Player 5]` | Player 1        | implemented in GS-02 |

### Method under test: `GameState(List<Player> players, Deck deck)` (defensive copy)

| Test Case                                                   | State of the System                                           | Action                                     | Expected output                                    | Implemented? |
|-------------------------------------------------------------|---------------------------------------------------------------|--------------------------------------------|----------------------------------------------------|--------------|
| GS-11: Changing the original list does not change the game  | created with list `[Player 1, Player 2]`                      | add Player 3 to the original list          | `getPlayers()` is still `[Player 1, Player 2]`     | :white_check_mark: |

### Method under test: `getPlayers()` (read-only)

| Test Case                                      | State of the System: `players` | Action                          | Expected output                                                          | Implemented? |
|------------------------------------------------|--------------------------------|---------------------------------|--------------------------------------------------------------------------|--------------|
| GS-12: Reject changes to the returned players  | `[Player 1, Player 2]`         | `getPlayers().add(Player 3)`    | Throws `UnsupportedOperationException`; players are `[Player 1, Player 2]` | :white_check_mark: |

### Method under test: `advanceCurrentPlayer()`

| Test Case                                                      | State of the System: `players` (eliminated marked with *) | Current player | Expected output                                                         | Implemented? |
|----------------------------------------------------------------|-----------------------------------------------------------|----------------|-------------------------------------------------------------------------|--------------|
| GS-13: Advance to the next player                              | `[P1, P2]`                                                | P1             | Returns `ADVANCED`; current player is P2                                | :white_check_mark: |
| GS-14: Wrap around from the last player of a 5-player game     | `[P1, P2, P3, P4, P5]`                                    | P5             | Returns `ADVANCED`; current player is P1                                | :white_check_mark: |
| GS-15: Skip an eliminated player                               | `[P1, P2*, P3]`                                           | P1             | Returns `ADVANCED`; current player is P3                                | :white_check_mark: |
| GS-16: Wrap around and skip an eliminated player               | `[P1*, P2, P3]`                                           | P3             | Returns `ADVANCED`; current player is P2                                | :white_check_mark: |
| GS-17: Advance after the current player is eliminated          | `[P1*, P2, P3]`                                           | P1             | Returns `ADVANCED`; current player is P2                                | :x:          |
| GS-18: One player remains (the current player)                 | `[P1, P2*]`                                               | P1             | Returns `ONE_PLAYER_REMAINS`; current player is P1                      | :x:          |
| GS-19: One player remains (another player)                     | `[P1*, P2]`                                               | P1             | Returns `ONE_PLAYER_REMAINS`; current player is P2                      | :x:          |
| GS-20: Reject advancing when no player is alive                | `[P1*, P2*]`                                              | P1             | Throws `IllegalStateException`; current player is still P1              | :x:          |

### Method under test: `getCurrentPlayer()` (after advancing)

| Test Case                                          | State of the System                     | Expected output | Implemented?         |
|----------------------------------------------------|-----------------------------------------|-----------------|----------------------|
| GS-21: Get the current player after one advance    | `[P1, P2]`, advanced once from P1       | P2              | implemented in GS-13 |
| GS-22: Get the current player after wrapping      | `[P1, P2, P3, P4, P5]`, advanced from P5 | P1              | implemented in GS-14 |
