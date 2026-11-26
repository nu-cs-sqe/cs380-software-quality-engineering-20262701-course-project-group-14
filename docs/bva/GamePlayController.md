# GamePlayController BVA

## Constructor under test: `GamePlayController(TurnActions actions, GamePlayViewPort view)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GPC-01 | No controller exists. | `actions = null`; valid view | Throws `NullPointerException` with `"Turn actions must not be null."`. | :white_check_mark: |
| GPC-02 | No controller exists. | Valid actions; `view = null` | Throws `NullPointerException` with `"Game play view must not be null."`. | :white_check_mark: |

## Method under test: `initialize()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GPC-03 | Actions expose a current snapshot. | None | Requests one snapshot and renders that exact snapshot once. | :white_check_mark: |

## Method under test: `handleDraw()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GPC-04 | `draw()` returns `NORMAL_CARD_DRAWN`. | None | Shows `draw.normal-card` and renders the latest snapshot. | :white_check_mark: |
| GPC-05 | `draw()` returns `EMPTY_DECK`. | None | Shows `draw.empty-deck` and renders the unchanged snapshot. | :white_check_mark: |
| GPC-06 | `draw()` returns `REJECTED_PENDING_DEFUSE`. | None | Shows `draw.pending-defuse` and renders the unchanged snapshot. | :white_check_mark: |
| GPC-07 | `draw()` returns `PLAYER_ELIMINATED`. | None | Shows `draw.player-eliminated` and renders the latest snapshot. | :white_check_mark: |
| GPC-08 | `draw()` returns `DEFUSE_POSITION_REQUIRED`; position request is cancelled. | None | Shows `draw.defuse-position-required`, renders, requests range `0..deckSize`, and does not call `resolveDefuse`. | :white_check_mark: |
| GPC-09 | `draw()` returns `DEFUSE_POSITION_REQUIRED`; a position is supplied. | None | Requests range `0..deckSize` and delegates the supplied position to `resolveDefuse`. | :white_check_mark: |

## Method under test: `handlePlayCard(int handIndex)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GPC-10 | `playCard` returns `CONTINUE_TURN` without revealed cards. | Valid index | Delegates the exact index, does not show future cards, and renders the latest snapshot. | :white_check_mark: |
| GPC-11 | `playCard` returns `CONTINUE_TURN` with revealed cards. | Valid index | Shows the revealed cards in order and renders the latest snapshot. | :white_check_mark: |
| GPC-12 | `playCard` returns `TURN_ENDED`. | Valid index | Renders the latest snapshot without showing a failure message. | :white_check_mark: |
| GPC-13 | `playCard` rejects with `INVALID_HAND_INDEX`. | Any index | Shows `play.invalid-hand-index` and renders. | :white_check_mark: |
| GPC-14 | `playCard` rejects with `CARD_NOT_PLAYABLE`. | Valid index | Shows `play.card-not-playable` and renders. | :white_check_mark: |
| GPC-15 | `playCard` rejects with `PRECONDITION_NOT_MET`. | Valid index | Shows `play.precondition-not-met` and renders. | :white_check_mark: |
| GPC-16 | `playCard` rejects with `DEFUSE_RESOLUTION_REQUIRED`. | Any index | Shows `play.defuse-required` and renders. | :white_check_mark: |

## Method under test: `handleChooseDefusePosition()`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GPC-17 | Snapshot deck size is `n`; position request is cancelled. | `OptionalInt.empty()` | Requests range `0..n`; does not call `resolveDefuse`, show a result message, or render. | :white_check_mark: |
| GPC-18 | Position is supplied; `resolveDefuse` returns `DEFUSED`. | A position in range | Shows `defuse.success` and renders the latest snapshot. | :white_check_mark: |
| GPC-19 | Position is supplied; result is `INVALID_POSITION`. | Any position | Shows `defuse.invalid-position` and renders the still-waiting snapshot. | :white_check_mark: |
| GPC-20 | Position is supplied; result is `NOT_AWAITING_DEFUSE`. | Any position | Shows `defuse.not-awaiting` and renders. | :white_check_mark: |
| GPC-21 | Position is supplied; result is `DEFUSE_NOT_FOUND`. | Any position | Shows `defuse.not-found` and renders. | :white_check_mark: |

## initialize() event bindings

### GPC-22 — Draw event

- Methods under test: initialize() and its registered draw callback.
- Implemented: :white_check_mark:
- State: Valid actions and view exist. The actions return a snapshot and a normal-draw result.
- Input: Initialize the controller, then trigger the view's draw event.
- Expected: draw() is called once, the normal-draw message appears, and the returned snapshot is rendered. Total render count is 2, including initialization.

### GPC-23 — Play-card event

- Methods under test: initialize() and its registered play callback.
- Implemented: :white_check_mark:
- State: Valid actions and view exist. The actions accept hand index 0 and return a continuing result.
- Input: Initialize the controller, then trigger the view's play event with index 0.
- Expected: playCard(0) is called once and the snapshot is rendered. Total render count is 2.

### GPC-24 — Defuse-position event

- Methods under test: initialize() and its registered position callback.
- Implemented: :white_check_mark:
- State: Valid actions and view exist. The snapshot has 4 cards in the deck. The view selects position 2 and the actions return DEFUSED.
- Input: Initialize the controller, then trigger the view's position-selection event.
- Expected: The view is asked for a position in [0, 4], resolveDefuse(2) is called once, the success message appears, and the snapshot is rendered. Total render count is 2.

## Method under test: `handlePlayCard(int handIndex)` for Cat Card selection

These cases use the proposed Week 9 view and command interfaces. The controller
collects selections and presents results. The engine validates each submitted
command and applies all game rules. Use a hand-written view adapter and exercise
the public controller entry. Each cancellation case starts with a fresh fixture.

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GPC-CAT-01 | The current hand is `[TACOCAT, SKIP, TACOCAT, BEARD_CAT]`. | Click hand index `0`. | The view starts an inline selection with original index `2` as its only candidate. | :white_check_mark: |
| GPC-CAT-02 | A matching second card is available. | Click the first card and cancel the inline selection. | Neither the target chooser nor a play entry is invoked. | :white_check_mark: |
| GPC-CAT-03 | Seat `0` is current, seat `1` is eliminated, seat `2` is active with an empty hand, and seat `3` is active with cards. | Select the second matching card. | The target chooser receives only seat `3` and receives no private card identities. | :white_check_mark: |
| GPC-CAT-04 | A second card and an eligible target are available. | Choose the second card and close the target chooser. | No play command or legacy single-card play is submitted. | :white_check_mark: |
| GPC-CAT-05 | Hand indices `0` and `2` match. Eligible targets are seats `2` and `3`, in that order. | Click `0`, choose `2`, then select seat `3` from the target chooser. | Submits `PlayCommand.pair(0, 2, 3)` exactly once and renders the latest snapshot when the result is `CONTINUE_TURN`. | :white_check_mark: |
| GPC-CAT-06 | A submitted pair is rejected with `INVALID_TARGET`. | Confirm both choices. | Shows localized rejection feedback, renders the latest snapshot, and submits no replacement command. | :white_check_mark: |
| GPC-CAT-07 | The legacy play entry rejects a single Cat Card with `INVALID_SELECTION`. | Click hand index `0` before the pair chooser is connected. | Shows the existing `play.card-not-playable` message and renders the latest snapshot. | :white_check_mark: |
| GPC-CAT-08 | The selected target's hand empties before submission and the engine returns `TARGET_HAND_EMPTY`. | Confirm the original choices. | Shows the existing rejection feedback, renders the latest snapshot, and submits once. | :white_check_mark: |
| GPC-CAT-09 | A second Cat Card matches and all other active players are empty-handed. | Choose the second card. | Shows the existing rejection feedback without opening a target chooser or submitting a play. | :white_check_mark: |

Use the same interaction for all five Cat Card types. Cover the production
assembly and all five types in [W9-IT05](../testing/cat-pair-integration.md).

## Method under test: `handlePlayCard(int handIndex)` for Sniff Test

The controller reads the clicked card type from the snapshot taken before the
play. Sniff Test reveals one card, but a Kitten that was sniffed has already
moved to the bottom, so the "top of the deck" preview must not show it.

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| GPC-SN-01 | The current hand is `[SNIFF_TEST]`; `playCard` returns `CONTINUE_TURN` revealing `[EXPLODING_KITTEN]`. | Click hand index `0`. | Shows `sniff.kitten-moved`, does not show the top-card preview, and renders the latest snapshot. | :white_check_mark: |
| GPC-SN-02 | The current hand is `[SNIFF_TEST]`; `playCard` returns `CONTINUE_TURN` revealing `[SKIP]`. | Click hand index `0`. | Shows the top-card preview `[SKIP]` and `sniff.safe`, and renders the latest snapshot. | :white_check_mark: |
| GPC-SN-03 | The current hand is `[SNIFF_TEST]`; `playCard` rejects with `PRECONDITION_NOT_MET`. | Click hand index `0`. | Shows only `play.precondition-not-met`, no preview, and renders. | :white_check_mark: |
| GPC-SN-04 | The current hand is `[SEE_THE_FUTURE]`; one card is left and `playCard` returns `CONTINUE_TURN` revealing `[EXPLODING_KITTEN]`. | Click hand index `0`. | Shows the preview `[EXPLODING_KITTEN]` as before and no Sniff Test message. | :x: |

## Gameplay events after a winner is declared

| ID | Method under test | State and input | Expected output | Implemented? |
|---|---|---|---|---|
| GPC-GO-01 | `handleDraw()` | The game has ended and a draw event reaches the controller. | Refreshes the terminal snapshot and preserves the winner message. | :white_check_mark: |
| GPC-GO-02 | `handlePlayCard(int)` | The game has ended and a card click reaches the controller. | Refreshes the terminal snapshot and preserves the winner message. | :white_check_mark: |
| GPC-GO-03 | `setOnReturnToSetup(Runnable)`, `handleReturnToSetup()` | The terminal view displays the winner. Register the setup callback and click the return button. | Runs the callback and returns to a fresh setup screen, preserving the old game. | :white_check_mark: |
| GPC-GO-04 | `handleReturnToSetup()` | A game is still in progress. A return event reaches the controller. | Leaves the active game in place. | :white_check_mark: |
| GPC-GO-05 | `setOnReturnToSetup(Runnable)` | A controller exists. Supply `null`. | Throws `NullPointerException`. | :white_check_mark: |

The JavaFX smoke test continues GPC-GO-03 by entering `2` on the setup screen
and starting a new game. It verifies that the same window shows eight starting
cards and enables drawing. The finished game's state is preserved.
