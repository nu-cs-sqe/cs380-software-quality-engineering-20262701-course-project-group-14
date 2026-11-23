# Week 9 Card Action Contracts

> Status: Proposed. These contracts become binding after team review. Existing
> Week 6 contracts remain in force unless this document explicitly extends
> them.

## Scope and Compatibility

- `TurnActions` keeps its existing methods and meanings.
- New command, Favor, and Nope capabilities use separate narrow interfaces so
  existing view stubs do not need unrelated methods.
- `TurnEngine` remains the only class that completes a turn, advances the
  current player, or declares the winner.
- The contract PR adds interfaces and value types only. Feature PRs add the
  corresponding production behavior and `implements` clauses.
- All player indices are stable, zero-based seats from the original game
  order. They are not indices into a filtered candidate list.
- All hand indices are zero-based positions in the hand before the command is
  executed.

## Play Commands

```java
public final class PlayCommand {
    public static PlayCommand single(int handIndex);
    public static PlayCommand targeted(
            int handIndex,
            int targetPlayerIndex
    );
    public static PlayCommand pair(
            int firstHandIndex,
            int secondHandIndex,
            int targetPlayerIndex
    );

    public int getFirstHandIndex();
    public OptionalInt getSecondHandIndex();
    public OptionalInt getTargetPlayerIndex();
}
```

The factories preserve the supplied indices but do not inspect a hand or game
state. `TurnEngine` validates bounds, distinct pair indices, matching card
types, legal targets, and the current phase atomically before changing state.
Negative indices are rejected by the factories with
`IllegalArgumentException`.

`playCard(int handIndex)` delegates to
`playCard(PlayCommand.single(handIndex))`. Both entries use the same phase
checks, validation, discard handling, Nope interception, and effect dispatch.
Favor and Cat Cards submitted through `single(...)` are rejected with
`PlayFailure.INVALID_SELECTION` without changing state.

```java
public interface CommandPlayActions {
    PlayResult playCard(PlayCommand command);
}
```

A `null` command is rejected with `NullPointerException`. Player selection
errors return an appropriate rejected `PlayResult`; they do not partially
remove or discard cards.

## Player Summaries

```java
public final class PlayerSummary {
    public static PlayerSummary create(
            int playerIndex,
            String name,
            int handSize,
            boolean eliminated
    );

    public int getPlayerIndex();
    public String getName();
    public int getHandSize();
    public boolean isEliminated();
}
```

Player indices and hand sizes cannot be negative. Names cannot be `null`,
empty, or blank. Summaries never expose another player's card types.

## Game Snapshot

`GameSnapshot` adds these immutable queries:

```java
public List<PlayerSummary> getPlayers();
public int getCurrentPlayerIndex();
public int getRemainingTurns();
public OptionalInt getWinnerIndex();
public OptionalInt getPendingFavorTargetIndex();
public OptionalInt getNopeResponderIndex();
```

- `getPlayers()` preserves stable seat order and returns an unmodifiable
  defensive copy.
- `getCurrentPlayerIndex()` is a stable player seat.
- `getRemainingTurns()` includes the current turn. A normal turn reports `1`;
  a player attacked for two turns progresses from `2` to `1`; a completed
  game reports `0`.
- Pending Defuse, Favor, and Nope phases preserve the unresolved turn count.
- `getWinnerIndex()` is present only in `GAME_OVER`.
- Pending responder queries are present only for their matching phase.
- Active snapshots reject a remaining-turn count of `0`. Game-over snapshots
  reject a positive remaining-turn count.

The existing `GameSnapshot.create(...)` remains source compatible during the
contract migration. Until a feature is connected, its new optional query is
empty and a non-game-over snapshot reports one remaining turn. The production
snapshot assembled by `TurnEngine` must use `GameState` and the engine's turn
debt as the real sources.

## Pending Actions

```java
public interface PendingAction {
    TurnPhase getPhase();
}
```

`TurnEngine` owns at most one pending-action slot. Defuse placement,
`FavorFlow`, and `NopeWindow` are mutually exclusive implementations. The
engine derives the public phase from that slot or from the game-over state.
Attack turn debt is stored independently.

| Phase | Permitted state-changing entry | Resolution |
|---|---|---|
| `ACTIONS_ALLOWED` | `draw()`, either `playCard(...)` entry | Performs an action or installs a pending action. |
| `AWAITING_DEFUSE_POSITION` | `resolveDefuse(position)` | A valid position consumes Defuse, reinserts the Kitten, and completes one turn. |
| `AWAITING_FAVOR_GIFT` | `submitFavorGift(playerIndex, handIndex)` | A valid gift transfers one card and continues the current turn. |
| `AWAITING_NOPE` | `respondToNope(playerIndex, playNope)` | Continues the response window or resolves the original effect. |
| `GAME_OVER` | None | The winner is fixed and gameplay writes are rejected. |

Snapshot reads are allowed in every phase. Any other write is rejected without
changing hands, deck, discard pile, pending action, current player, or turn
debt.

## Result Extensions

The existing enums add these values:

```java
// TurnPhase
AWAITING_FAVOR_GIFT,
AWAITING_NOPE,
GAME_OVER

// DrawStatus
REJECTED_PENDING_ACTION,
GAME_OVER

// PlayStatus
AWAITING_FAVOR_GIFT,
AWAITING_NOPE,
CANCELLED_BY_NOPE

// PlayFailure
INVALID_SELECTION,
INVALID_TARGET,
TARGET_HAND_EMPTY,
ACTION_PENDING,
NO_PENDING_ACTION,
INVALID_RESPONDER,
NOPE_NOT_FOUND,
GAME_OVER
```

Existing Defuse-specific statuses remain unchanged. `PlayStatus.TURN_ENDED`
means one required turn finished; an attacked player may remain current with
one required turn still outstanding.

## Favor Actions

```java
public interface FavorActions {
    List<CardType> getFavorChoices(int respondingPlayerIndex);
    PlayResult submitFavorGift(
            int respondingPlayerIndex,
            int giftHandIndex
    );
}
```

Only the pending Favor target may retrieve or submit choices. A valid gift
returns `CONTINUE_TURN`. Closing the view does not submit or cancel the pending
action. Invalid responders and indices preserve all state.

## Nope Actions

```java
public interface NopeActions {
    boolean canPlayNope(int respondingPlayerIndex);
    PlayResult respondToNope(
            int respondingPlayerIndex,
            boolean playNope
    );
}
```

Nope is playable only through an active Nope response window. Clicking a Nope
in `ACTIONS_ALLOWED` returns `CARD_NOT_PLAYABLE`. Response order uses stable
seat order and does not reveal publicly who holds a Nope. A closed private
panel leaves the response pending; an explicit pass advances the window.

The proposed same-device rule prevents the latest card player from immediately
countering that same card. After another player adds a Nope, the previous card
player may respond in the new round. Kitten and Defuse do not open a Nope
window.

## View Selections and Private Handoffs

The view port is extended by feature-specific narrow interfaces rather than by
adding every callback to the existing base interface.

```java
OptionalInt requestSecondCard(List<Integer> matchingHandIndices);
OptionalInt requestTargetPlayer(List<PlayerSummary> candidates);
OptionalInt requestFavorGift(
        PlayerSummary giver,
        List<CardType> privateHand
);
Optional<NopeDecision> requestNopeResponse(
        PlayerSummary responder,
        boolean hasNope
);
void hidePrivateCards();
```

Second-card selection returns an original hand index. Target selection returns
a stable player seat. `OptionalInt.empty()` cancels a card or target selection.
`Optional.empty()` closes a Nope panel without passing. The controller gathers
all selections before submitting one command. A cancellation never calls the
engine.

Favor and Nope views hide the previous player's hand and future-card preview
before showing another player's private information. This is a same-device UI
privacy convention, not an authorization boundary.

## Random Source

```java
public interface RandomSource {
    int nextInt(int bound);
}
```

`nextInt(bound)` returns a value in `0..bound - 1`; non-positive bounds are
invalid. Deck shuffling and Cat Card stealing use the same injected abstraction.
Production assembly supplies a default random source; tests and fixed games
supply deterministic implementations.

Existing Deck contracts remain unchanged:

```java
public List<Card> peekTop(int count);
public void insertAt(Card card, int position);
public void shuffle();
```

`peekTop` rejects negative counts, returns an empty list for zero, and returns
the whole deck when the count exceeds the deck size. `insertAt` uses position
zero as the top and `size()` as the bottom.

## Ownership and Integration Rules

- Xiaoyi owns the shared contracts, pending-action slot, common play-command
  path, Cat Cards, Attack registration, and game-over UI.
- Qingyang owns `FavorFlow`, Defuse, elimination, and their private UI flow.
- Sifan owns `NopeWindow`, Shuffle, See the Future, and deterministic Deck
  support.
- Favor and Nope rules stay in their collaboration classes. Their
  `TurnEngine` changes are pure delegation and receive an additional review by
  Xiaoyi.
- `MainApp` calls a testable assembly factory and contains no game rules.
- All new user-visible text uses resource keys, and supported bundles must have
  identical key sets.

## Confirmed Decisions

- [ ] Confirm all public signatures and package placement.
- [ ] Confirm the complete phase table and the single pending-action rule.
- [ ] Confirm stable-seat and original-hand-index semantics.
- [ ] Confirm Cat Card and Deck use of the shared random source.
- [ ] Confirm Nope scope and the same-device self-counter restriction.
- [ ] Confirm the `GameSnapshot` migration defaults.
