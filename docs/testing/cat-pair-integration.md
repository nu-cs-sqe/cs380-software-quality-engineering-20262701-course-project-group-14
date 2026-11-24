# W9-IT05: Cat Card Pair Play

- **Owner:** Xiaoyi Wang
- **Status:** All five pair-play examples and the single-card rejection passed
  through the factory on the local draft-engine baseline. Production startup
  and manual verification are pending.
- **Controller check:** All five types also passed through the registered
  play callback with a test view. The scenario selects original hand index `2`
  and seat `3` after filtering eliminated and empty-handed players.
- **Related:** [Cat Card pair-play BVA](../bva/CatPairPlay.md), CAT-01,
  CAT-12, CAT-13, and CAT-24

Use the same game assembly as `MainApp`, with real players, game state,
deck, discard pile, and engine. Supply a fixed deck and a deterministic
selection source through the agreed integration entry. Run each example with
fresh state. A view adapter can drive the interaction without launching JavaFX.

## Scenario outline: Play a matching pair and stay on the same turn

- Given seat `0` is current in `ACTIONS_ALLOWED` with one turn remaining.
- And seat `0` holds `[<cat>, <cat>, SKIP]`, seat `1` holds `[ATTACK, FAVOR]`,
  the discard pile is empty, and the draw pile is `[SHUFFLE, NOPE]` from top
  to bottom.
- And the card-selection source returns `1` for a bound of `2`.
- When the current player submits `PlayCommand.pair(0, 1, 1)` through
  the game's command entry.
- Then the result is `CONTINUE_TURN` with `PlayFailure.NONE`.
- And the resulting hands are `[SKIP, FAVOR]` and `[ATTACK]` respectively.
- And the discard pile grows by two. Verify both discarded cards directly
  when the public discard interface exposes their identities.
- And seat `0` remains current with one turn left. The draw-pile order and
  `ACTIONS_ALLOWED` phase stay the same.

| Example | Cat type |
|---|---|
| 1 | `TACOCAT` |
| 2 | `CATTERMELON` |
| 3 | `HAIRY_POTATO_CAT` |
| 4 | `BEARD_CAT` |
| 5 | `RAINBOW_RALPHING_CAT` |

## Scenario: Reject a Cat Card through the single-card entry

- Given the same starting state with a `TACOCAT` at hand index `0`.
- When the current player submits index `0` through `playCard(int)`.
- Then the result is `REJECTED` with `INVALID_SELECTION`.
- And both hands, discard count, draw-pile order, current seat, remaining
  turns, and phase stay the same.

## Record after execution

Record the tested revision, exact Gradle command, parameterized results, and
PR in the existing Integration Testing Plan on the Project Board. Track this
acceptance under W9-IT05. Run the manual prompt and cancellation checks in
the actual application once fixed-game startup is available.
