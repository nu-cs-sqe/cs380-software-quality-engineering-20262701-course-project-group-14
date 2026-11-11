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
| GPC-19 | Position is supplied; result is `INVALID_POSITION`. | Any position | Shows `defuse.invalid-position` and renders the still-waiting snapshot. | :x: |
| GPC-20 | Position is supplied; result is `NOT_AWAITING_DEFUSE`. | Any position | Shows `defuse.not-awaiting` and renders. | :x: |
| GPC-21 | Position is supplied; result is `DEFUSE_NOT_FOUND`. | Any position | Shows `defuse.not-found` and renders. | :x: |
