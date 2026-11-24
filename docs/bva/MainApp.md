# MainApp setup navigation BVA

## Method under test: `startGame(String, Consumer<GameState>, Consumer<String>)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| NAV-01 | The setup screen is open and no game has started. | `"2"` | Sends the initialized two-player game to the gameplay screen. | :white_check_mark: |
| NAV-02 | The setup screen is open and no game has started. | `"1"` | Reports the player-count error and leaves the setup screen active. | :white_check_mark: |

## Methods under test: `showGameplay(Scene, GameState)` and `startGameplay(GameState, GamePlayViewPort)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| NAV-CAT-01 | A valid fixed game has a matching Cat Card pair and one eligible target. | Open gameplay, cancel each chooser in separate attempts, then confirm both choices. | Opens the playable JavaFX view with the shared engine. Cancellation preserves both hands. A confirmed pair transfers the selected card, updates the rendered hand, and keeps the current player. | :white_check_mark: |
