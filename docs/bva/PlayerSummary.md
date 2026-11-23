# PlayerSummary BVA

## Method under test: `create(int playerIndex, String name, int handSize, boolean eliminated)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| PS-01 | No summary exists. | `playerIndex = 0`, `name = "Player 1"`, `handSize = 0`, `eliminated = false` | Creates an active-player summary preserving all values. | :x: |
| PS-02 | No summary exists. | `playerIndex = 4`, `name = "Player 5"`, `handSize = 8`, `eliminated = true` | Creates an eliminated-player summary preserving all values. | :x: |
| PS-03 | No summary exists. | `playerIndex = -1`, otherwise valid values | Throws `IllegalArgumentException` with `"Player index must not be negative."`; no summary is created. | :x: |
| PS-04 | No summary exists. | `name = null`, otherwise valid values | Throws `NullPointerException` with `"Player name must not be null."`; no summary is created. | :x: |
| PS-05 | No summary exists. | `name = ""`, otherwise valid values | Throws `IllegalArgumentException` with `"Player name must not be blank."`; no summary is created. | :x: |
| PS-06 | No summary exists. | `name = "   "`, otherwise valid values | Throws `IllegalArgumentException` with `"Player name must not be blank."`; no summary is created. | :x: |
| PS-07 | No summary exists. | `handSize = -1`, otherwise valid values | Throws `IllegalArgumentException` with `"Hand size must not be negative."`; no summary is created. | :x: |

## Getter coverage

| Public method | Covered by |
|---|---|
| `getPlayerIndex()` | PS-01, PS-02 |
| `getName()` | PS-01, PS-02 |
| `getHandSize()` | PS-01, PS-02 |
| `isEliminated()` | PS-01, PS-02 |
