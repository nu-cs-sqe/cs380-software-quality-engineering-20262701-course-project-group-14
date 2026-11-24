# Cat Card Pair Play BVA

> Status: Proposed. Checked cases have passed on the local draft-engine
> baseline. Verify them again when the reviewed dependencies are integrated.

## Methods under test

- `CommandPlayActions.playCard(PlayCommand command)` is the shared command entry.
- `TurnActions.playCard(int handIndex)` is the existing single-card entry. Cover its compatibility separately.

All hands and seats use zero-based indices. A hand index refers to the original
hand at submission. A target index is the player's stable seat. Unless a row
says otherwise, A is the current player in seat `0`, B is the active target in
seat `1`, and B holds at least one card. A has one remaining turn in
`ACTIONS_ALLOWED`. Each rejection preserves the players' hands, draw-pile order,
discard state, current player, remaining turns, and pending action. Observe
these values through public queries and the fixture's real objects.

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| CAT-01 | A holds `[TACOCAT, TACOCAT, SKIP]`; B in seat 1 holds `[ATTACK, FAVOR]`. | `pair(0, 1, 1)`; fixed random result `1` | Returns `CONTINUE_TURN / NONE`; A holds `[SKIP, FAVOR]`; B holds `[ATTACK]`; two Tacocats are discarded. A remains current and the draw pile and remaining turns are unchanged. | :white_check_mark: |
| CAT-02 | A's matching cards are at positions 0 and 2, with another card between them. | `pair(2, 0, 1)` | Transfers one card, discards the two selected cards, and preserves the intervening card. | :white_check_mark: |
| CAT-03 | A holds a matching pair. | `pair(0, 0, 1)` | Returns `REJECTED / INVALID_SELECTION`. | :white_check_mark: |
| CAT-04 | Selected cards are two different Cat Card types. | Select both cards and a valid target. | Returns `REJECTED / INVALID_SELECTION`. | :white_check_mark: |
| CAT-05 | A holds one Cat Card and one non-cat card. | Select both cards and a valid target. | Returns `REJECTED / INVALID_SELECTION`. Confirm mixed-type mapping in W9-A review. | :white_check_mark: |
| CAT-06 | A's hand size is `n`. | First hand index equals `n`; second index is valid. | Returns `REJECTED / INVALID_HAND_INDEX`. | :white_check_mark: |
| CAT-07 | A's hand size is `n`. | First hand index is valid; second index equals `n`. | Returns `REJECTED / INVALID_HAND_INDEX`. | :white_check_mark: |
| CAT-08 | A is in seat 0. | Select a matching pair and target seat 0. | Returns `REJECTED / INVALID_TARGET`. | :white_check_mark: |
| CAT-09 | The original table has `n` seats. | Select a matching pair and target seat `n`. | Returns `REJECTED / INVALID_TARGET`. | :white_check_mark: |
| CAT-10 | A and B are active; C in seat 2 has been eliminated. | Select a matching pair and target seat 2. | Returns `REJECTED / INVALID_TARGET`. | :white_check_mark: |
| CAT-11 | The selected seat belongs to another active player with an empty hand. | Select a matching pair and that seat. | Returns `REJECTED / TARGET_HAND_EMPTY`. | :white_check_mark: |
| CAT-12 | A's first card is a Cat Card. | `playCard(PlayCommand.single(0))` | Returns `REJECTED / INVALID_SELECTION`. | :white_check_mark: |
| CAT-13 | A's first card is a Cat Card. | `playCard(0)` | Returns the same `REJECTED / INVALID_SELECTION` as the command entry. | :white_check_mark: |
| CAT-14 | A selected a Cat Card and a legal target. | `playCard(PlayCommand.targeted(0, 1))` | Returns `REJECTED / INVALID_SELECTION` because the second card is absent. Confirm this mapping in W9-A review. | :white_check_mark: |
| CAT-15 | The target holds exactly one card. | Select a legal pair; fixed random result `0`. | Transfers the target's only card and continues A's turn. | :white_check_mark: |
| CAT-16 | B holds `[ATTACK, SKIP, FAVOR]`. | Select a legal pair; fixed random result `targetHandSize - 1`, which is `2`. | Transfers `FAVOR` and leaves B's remaining hand in order. | :white_check_mark: |
| CAT-17 | A legal pair exists and A has two outstanding turns after an Attack. | Submit the pair and a valid target. | Returns `CONTINUE_TURN`; A remains current with two turns outstanding. | :white_check_mark: |
| CAT-18 | Defuse placement is pending. | Submit an otherwise valid pair. | Returns `REJECTED / DEFUSE_RESOLUTION_REQUIRED`; Defuse remains pending. | :white_check_mark: |
| CAT-19 | A Favor gift is pending. | Submit an otherwise valid pair. | Returns `REJECTED / ACTION_PENDING`; the Favor responder and state are preserved. | :x: |
| CAT-20 | A Nope response is pending. | Submit an otherwise valid pair. | Returns `REJECTED / ACTION_PENDING`; the Nope responder and state are preserved. | :x: |
| CAT-21 | The game is over. | Submit an otherwise valid pair. | Returns `REJECTED / GAME_OVER`; the winner is preserved. | :x: |
| CAT-22 | A game is in progress. | `playCard((PlayCommand) null)` | Throws `NullPointerException`; gameplay state is preserved. | :white_check_mark: |
| CAT-23 | Normal actions are allowed and the legacy entry is available. | `playCard(-1)` | Preserves the prior `REJECTED / INVALID_HAND_INDEX` behavior. Confirm the adapter behavior in W9-A review. | :white_check_mark: |
| CAT-24 | A holds two cards of one supported type, tested separately for all five types. | Submit a legal pair and a valid target with a fixed random result. | `TACOCAT`, `CATTERMELON`, `HAIRY_POTATO_CAT`, `BEARD_CAT`, and `RAINBOW_RALPHING_CAT` each produce the expected discard and transfer without ending the turn. | :white_check_mark: |
