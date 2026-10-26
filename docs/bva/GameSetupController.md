# GameSetupController BVA

## Method under test: `startGame(String playerCountText)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---:|---|---|
| GSC-01 | No ready game exists; setup dependencies contain sufficient cards. | `"1"` | Validation fails and no ready `GameState` is produced. | :white_check_mark: |
| GSC-02 | No ready game exists; setup dependencies contain sufficient cards. | `"2"` | Returns a ready state with 2 players, 8 cards and at least 1 Defuse per player, 1 Exploding Kitten in the remaining shuffled deck, and Player 1 current. | :white_check_mark: |
| GSC-03 | No ready game exists; setup dependencies contain sufficient cards. | `"3"` | Returns a ready state with 3 players, 8 cards and at least 1 Defuse per player, 2 Exploding Kittens in the remaining shuffled deck, and Player 1 current. | :white_check_mark: |
| GSC-04 | No ready game exists; setup dependencies contain sufficient cards. | `"4"` | Returns a ready state with 4 players, 8 cards and at least 1 Defuse per player, 3 Exploding Kittens in the remaining shuffled deck, and Player 1 current. | :white_check_mark: |
| GSC-05 | No ready game exists; setup dependencies contain sufficient cards. | `"5"` | Returns a ready state with 5 players, 8 cards and at least 1 Defuse per player, 4 Exploding Kittens in the remaining shuffled deck, and Player 1 current. | :x: |
| GSC-06 | No ready game exists; setup dependencies contain sufficient cards. | `"6"` | Validation fails and no ready `GameState` is produced. | :x: |
| GSC-07 | No ready game exists. | `null` | Validation fails and no ready `GameState` is produced. | :x: |
| GSC-08 | No ready game exists. | `""` | Validation fails and no ready `GameState` is produced. | :x: |
| GSC-09 | No ready game exists. | `"   "` | Validation fails and no ready `GameState` is produced. | :x: |
| GSC-10 | No ready game exists. | `"abc"` | Validation fails with a clear message that a whole-number player count from 2 through 5 is required; no ready `GameState` is produced. | :x: |
| GSC-11 | No ready game exists. | `"2.5"` | Validation fails with a clear message that a whole-number player count from 2 through 5 is required; no ready `GameState` is produced. | :x: |
| GSC-12 | No ready game exists; setup dependencies contain sufficient cards. | `" 3 "` | Surrounding whitespace is ignored and the same ready state as input `"3"` is returned. | :x: |