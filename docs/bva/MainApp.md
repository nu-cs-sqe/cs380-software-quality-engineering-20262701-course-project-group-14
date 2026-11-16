# MainApp setup navigation BVA

## Method under test: `startGame(String, Consumer<GameState>, Consumer<String>)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| NAV-01 | The setup screen is open and no game has started. | `"2"` | Sends the initialized two-player game to the gameplay screen. | :white_check_mark: |
| NAV-02 | The setup screen is open and no game has started. | `"1"` | Reports the player-count error and leaves the setup screen active. | :x: |
