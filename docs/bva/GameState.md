# GameState BVA

### Method under test: `GameState(List<Player> players, Deck deck)`

| Test Case                                                     | State of the System: `players`                       | State of the System: `deck` | Expected output                                                                                                                                | Implemented?       |
|---------------------------------------------------------------|------------------------------------------------------|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|--------------------|
| GS-01: Create a game state with the minimum number of players | `[Player 1, Player 2]`                               | a valid deck                | `getPlayers()` is `[Player 1, Player 2]`; `getCurrentPlayer()` is Player 1; `getDeck()` is the same deck object                                | :white_check_mark: |
| GS-02: Create a game state with the maximum number of players | `[Player 1, Player 2, Player 3, Player 4, Player 5]` | a valid deck                | `getPlayers()` is `[Player 1, Player 2, Player 3, Player 4, Player 5]` (first is Player 1, last is Player 5); `getCurrentPlayer()` is Player 1 | :white_check_mark: |
| GS-03: Reject a null player list                              | `null`                                               | a valid deck                | Throws `NullPointerException`, "Players must not be null."                                                                                     | :x:                |
| GS-04: Reject an empty player list                            | `[]`                                                 | a valid deck                | Throws `IllegalArgumentException`, "Players must not be empty."                                                                                | :x:                |
| GS-05: Reject a null deck                                     | `[Player 1, Player 2]`                               | `null`                      | Throws `NullPointerException`, "Deck must not be null."                                                                                        | :x:                |

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