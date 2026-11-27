# Original Card Proposal: Sniff Test

> Status: Proposed by Xiaoyi Wang. To be confirmed by the team.

## Card

| Field | Value |
|---|---|
| Name | Sniff Test |
| `CardType` | `SNIFF_TEST` |
| Kind | Single action card; no target and no second card |
| Ends the turn | No |

**Effect.** The current player sniffs the top card of the draw pile. If it is an
Exploding Kitten, that Kitten is moved to the bottom of the draw pile. Otherwise
the draw pile is not changed. The sniffed card type is shown only to the current
player. The turn continues, and the player still has to end it by drawing or by
playing a card that ends the turn.

## Why this card

- See the Future shows three cards but changes nothing; Shuffle changes the
  whole pile but shows nothing; Skip avoids one draw. Sniff Test sits between
  them: it looks at one card and acts only when that card is dangerous.
- It never removes a Kitten from the game. A buried Kitten still has to be drawn
  eventually, so the number of players who can explode does not change.

## Play conditions

- Playable only in `ACTIONS_ALLOWED`. As with every other card, `TurnEngine`
  rejects it while a Defuse position, Favor gift or Nope response is pending,
  and after the game is over.
- The draw pile must contain at least one card. With an empty draw pile,
  `canPlay` returns `false` and the card stays in the player's hand.

## Player choices

None. The player only decides whether to play the card.

## State changes

| Draw pile before (top → bottom) | Draw pile after | Shown to the current player |
|---|---|---|
| `[EXPLODING_KITTEN, x, y]` | `[x, y, EXPLODING_KITTEN]` | `EXPLODING_KITTEN` |
| `[x, EXPLODING_KITTEN, y]` | unchanged | `x` |
| `[EXPLODING_KITTEN]` | unchanged (the top is also the bottom) | `EXPLODING_KITTEN` |
| `[]` | cannot be played | nothing |

In every case the played Sniff Test goes to the discard pile, the player's other
cards and the current player stay the same, and only the sniffed Kitten moves.
The same Kitten object is moved; no card is created or removed.

## Nope

Sniff Test can be Noped like other action cards. When the Nope window is
available, a Noped Sniff Test is discarded without looking at or moving any
card. Until then it resolves immediately.

## Examples

- **Success:** Player 1 holds Sniff Test. The draw pile is
  `[EXPLODING_KITTEN, SKIP, ATTACK]`. Player 1 plays Sniff Test, sees
  `EXPLODING_KITTEN`, and the pile becomes `[SKIP, ATTACK, EXPLODING_KITTEN]`.
  Player 1 is still the current player and safely draws `SKIP`.
- **Boundary:** The draw pile is `[EXPLODING_KITTEN]`. Player 1 plays Sniff Test
  and sees `EXPLODING_KITTEN`; the top card is also the bottom card, so the pile
  is unchanged and the next draw is still the Kitten.

## Deck composition

The standard deck built by `Deck()` stays the 56-card Original Edition deck and
contains no original cards. The number of Sniff Test copies is decided together
with the other original cards; they are then added during game setup, in the
same way that extra Defuse and Exploding Kitten cards are added today.

## Implementation status

| Part | Where | Status |
|---|---|---|
| Effect (`SniffTestEffect`), BVA `SN-01`–`SN-11` | PR #49 → `main` | Done |
| `Deck()` keeps the 56-card deck (`Deck39`) | PR #49 → `main` | Done |
| Card face: paw icon, `ROLE_SNIFF` colour, effect text in `CardCatalog` | PR #49 → `main` | Done |
| Registration in `TurnEngineFactory` (`TEF-07`, `TEF-08`) | `feature/sniff-test-wiring`, stacked on the turn-engine branches | Done; reaches `main` with the turn engine |
| Result feedback in `GamePlayController` (`GPC-SN-01`–`GPC-SN-05`) and text in `i18n/messages.properties` (`sniff.*` keys) | `feature/sniff-test-wiring` | Done |
| Copies added to the game during setup | — | Waiting for the team decision below |

After a sniffed Kitten moves to the bottom, the controller shows
`sniff.kitten-moved` instead of the "top of the deck" preview, because that
Kitten is no longer on top. For a safe card it shows the one-card preview and
`sniff.safe`.

## Needs team confirmation

1. **Copies per game.** Proposal: 4 copies, added during setup together with the
   other original cards. With 4 copies the remaining draw pile after setup
   grows by 4 (2 players: 35 → 39; 3: 29 → 33; 4: 23 → 27; 5: 16 → 20), so the
   Setup integration test expectations change once, when the original cards are
   added.
2. **Shared exclusion rule.** Every original card type is listed in
   `Deck.ORIGINAL_CARDS` so that `Deck()` stays the 56-card Original Edition
   deck. Other original cards can be added to the same set.
3. **Nope.** The proposal lets Nope cancel Sniff Test like other action cards.
   Confirm this when the Nope window is designed.
4. **Same-screen privacy.** The sniffed card is shown on the shared screen in
   the same way as See the Future. Confirm that this is acceptable.

## Implementation outline

- `SniffTestEffect implements CardEffect` in `domain`, with BVA in
  `docs/bva/SniffTestEffect.md`.
- The sniffed card type is returned through `EffectOutcome.getRevealedCards()`,
  so the existing reveal path in `GamePlayController` shows it without a new
  view method.
- Registration in `TurnEngineFactory`, the card face in `CardCatalog`, resource
  text, and an integration test through the factory follow once the turn engine
  is on `main`.
