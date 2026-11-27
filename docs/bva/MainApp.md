# MainApp setup navigation BVA

## Method under test: `startGame(String, Consumer<GameState>, Consumer<String>)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| NAV-01 | The setup screen is open and no game has started. | `"2"` | Sends the initialized two-player game to the gameplay screen. | :white_check_mark: |
| NAV-02 | The setup screen is open and no game has started. | `"1"` | Reports the player-count error and leaves the setup screen active. | :white_check_mark: |

## Methods under test: `showGameplay(Scene, GameState)` and `bindGameplayNavigation(Scene, GamePlayController)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| NAV-CAT-01 | A valid fixed game has a matching Cat Card pair and exactly one eligible target. | Open gameplay and click one of the two matching hand cards once. | The pair is played at once with the only eligible target: the target's card moves to the current hand, both Tacocats show on the discard pile, and the current player keeps the turn. No chooser opens. | :white_check_mark: |
| NAV-CAT-02 | A valid fixed game has a matching Cat Card pair and two eligible targets. | Click a matching card and press Escape; click one again and press the draw pile; click one again and press the second target's seat. | While choosing, both target seats are marked targetable and both pair cards are lifted. Escape and a click away cancel without changing any hand or drawing. Pressing a seat steals from that player only, and the current player keeps the turn. | :white_check_mark: |

## Method under test: startGameplay(GameState, GamePlayViewPort)

### NAV-03 — Initialize a gameplay session

- Implemented: :white_check_mark:
- State: Two living players each hold 8 cards. Player 1 is current. The deck contains [ATTACK, SHUFFLE].
- Input: A valid state and recording view.
- Expected: The view receives Player 1's initial snapshot, an empty discard pile, two living players and ACTIONS_ALLOWED. Draw, play and position-selection callbacks are registered.

### NAV-04 — Draw through the view

- Implemented: :white_check_mark:
- State: A session has been initialized using the fixed state from NAV-03.
- Input: Trigger the view's draw event.
- Expected: Player 1 receives ATTACK and holds 9 cards. Player 2 becomes current. The view shows Player 2's 8 cards, one remaining deck card and an empty discard pile.

### NAV-05 — Play Skip through the view

- Implemented: :white_check_mark:
- State: The fixed session is initialized, and Player 1 has SKIP at index 0.
- Input: Trigger the view's play event with index 0.
- Expected: Player 1 retains the other 7 cards, one card enters the discard pile, and Player 2 becomes current. Both deck cards remain in their original order.

### NAV-06 — Reject a null state

- Implemented: :white_check_mark:
- State: A valid recording view exists.
- Input: state = null.
- Expected: Throws NullPointerException with "Game state must not be null.".

### NAV-07 — Reject a null view

- Implemented: :white_check_mark:
- State: The valid fixed state from NAV-03 exists.
- Input: view = null.
- Expected: Throws NullPointerException with "Game play view must not be null.". Current player and deck size remain unchanged.

### NAV-08 — Play Attack through the gameplay startup

- Implemented: :white_check_mark:
- State: The fixed session is initialized, and Player 1 holds Attack at index 3.
- Input: Play Attack, then trigger the draw event twice.
- Expected: Attack leaves Player 1's hand, and one card enters the discard pile. Player 2 stays current after the first draw and holds both drawn cards after the second. Player 1 then becomes current.
