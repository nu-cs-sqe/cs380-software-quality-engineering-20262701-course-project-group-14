# SniffTestEffect BVA

Original card proposal: `docs/design/original-card-sniff-test.md`.

Decks below are written from top to bottom. "Unchanged" means the same card
objects remain in the same order. The player's hand is never changed by the
effect; `TurnEngine` removes and discards the played Sniff Test card.

## Method under test: `canPlay(Player, Deck)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| SN-01 | Deck is `[SKIP]`; player holds one card. | Valid player and deck | Returns `true`; deck and hand are unchanged. | :white_check_mark: |
| SN-02 | Deck is empty. | Valid player and deck | Returns `false`; deck stays empty and the hand is unchanged. | :white_check_mark: |
| SN-03 | Deck is `[SKIP]`. | `player = null` | Throws `NullPointerException` with `"Player must not be null."`; deck is unchanged. | :x: |
| SN-04 | A player exists. | `deck = null` | Throws `NullPointerException` with `"Deck must not be null."`; hand is unchanged. | :x: |

## Method under test: `play(Player, Deck)`

| ID | State of the system | Input | Expected output | Implemented? |
|---|---|---|---|---|
| SN-05 | Deck is `[EXPLODING_KITTEN, SKIP, ATTACK]`. | Valid player and deck | Deck becomes `[SKIP, ATTACK, EXPLODING_KITTEN]` with the same Kitten object at the bottom. Returns an outcome that does not end the turn and reveals `[EXPLODING_KITTEN]`. | :x: |
| SN-06 | Deck is `[SKIP, EXPLODING_KITTEN]`. | Valid player and deck | Deck is unchanged. Returns an outcome that does not end the turn and reveals `[SKIP]`. | :x: |
| SN-07 | Deck is `[EXPLODING_KITTEN]` (the top card is also the bottom card). | Valid player and deck | Deck is unchanged. Returns an outcome that does not end the turn and reveals `[EXPLODING_KITTEN]`. | :x: |
| SN-08 | Deck is `[EXPLODING_KITTEN (A), EXPLODING_KITTEN (B), SKIP]`. | Valid player and deck | Only the top Kitten moves: deck becomes `[B, SKIP, A]`. Reveals `[EXPLODING_KITTEN]`. | :x: |
| SN-09 | Deck is empty. | Valid player and deck | Throws `IllegalStateException` with `"Sniff Test requires a non-empty deck."`; deck stays empty. | :x: |
| SN-10 | Deck is `[EXPLODING_KITTEN, SKIP]`. | `player = null` | Throws `NullPointerException` with `"Player must not be null."`; deck is unchanged. | :x: |
| SN-11 | A player exists. | `deck = null` | Throws `NullPointerException` with `"Deck must not be null."`; hand is unchanged. | :x: |
