# Turn Contracts

> Confirmed by the team and merged through PR #24. Production implementations
> must follow these contracts unless the team approves a documented revision.
> 
## Package Placement

- `domain`: result enums, result classes, `EffectOutcome`, `GameSnapshot`,
  `TurnActions`, and `CardEffect`
- `controller`: `GamePlayViewPort` and `GamePlayController`
- `ui`: JavaFX implementations such as `GamePlayView`

## Result Enums

```java
public enum TurnPhase {
    ACTIONS_ALLOWED,
    AWAITING_DEFUSE_POSITION
}

public enum DrawStatus {
    NORMAL_CARD_DRAWN,
    DEFUSE_POSITION_REQUIRED,
    PLAYER_ELIMINATED,
    EMPTY_DECK,
    REJECTED_PENDING_DEFUSE
}

public enum PlayStatus {
    CONTINUE_TURN,
    TURN_ENDED,
    REJECTED
}

public enum PlayFailure {
    NONE,
    INVALID_HAND_INDEX,
    CARD_NOT_PLAYABLE,
    PRECONDITION_NOT_MET,
    DEFUSE_RESOLUTION_REQUIRED
}

public enum DefuseResult {
    DEFUSED,
    INVALID_POSITION,
    NOT_AWAITING_DEFUSE,
    DEFUSE_NOT_FOUND
}

public enum AdvanceResult {
    ADVANCED,
    ONE_PLAYER_REMAINS
}

public enum ExplosionOutcome {
    DEFUSE_POSITION_REQUIRED,
    PLAYER_ELIMINATED
}
```

## Result Classes

### DrawResult

```java
public static DrawResult normal(CardType drawnCardType);
public static DrawResult defuseRequired();
public static DrawResult playerEliminated();
public static DrawResult emptyDeck();
public static DrawResult rejectedPendingDefuse();
public DrawStatus getStatus();
public Optional<CardType> getDrawnCardType();
```

`getDrawnCardType()` never returns `null`. Only `normal(...)` produces a
non-empty `Optional`.

### PlayResult

```java
public static PlayResult continueTurn(List<CardType> revealedCards);
public static PlayResult turnEnded();
public static PlayResult rejected(PlayFailure failure);
public PlayStatus getStatus();
public PlayFailure getFailure();
public List<CardType> getRevealedCards();
```

The revealed-card list is a defensive copy and cannot be modified. Successful
results use `PlayFailure.NONE`; rejected results contain an actual failure.

### EffectOutcome

```java
EffectOutcome(boolean endsTurn, List<CardType> revealedCards);
public boolean endsTurn();
public List<CardType> getRevealedCards();
```

The constructor is package-private so card effects in `domain` can create an
outcome without exposing outcome construction to the Controller or View.

### GameSnapshot

```java
public static GameSnapshot create(
        String currentPlayerName,
        List<CardType> currentPlayerHand,
        int deckSize,
        int discardPileSize,
        int alivePlayerCount,
        TurnPhase phase
);

public String getCurrentPlayerName();
public List<CardType> getCurrentPlayerHand();
public int getDeckSize();
public int getDiscardPileSize();
public int getAlivePlayerCount();
public TurnPhase getPhase();
```

The hand is an unmodifiable defensive copy. Card counts cannot be negative,
and at least one player must be alive.

## Module Interfaces

### TurnActions

```java
public interface TurnActions {
    DrawResult draw();
    PlayResult playCard(int handIndex);
    DefuseResult resolveDefuse(int position);
    TurnPhase getPhase();
    GameSnapshot getSnapshot();
}
```

`TurnEngine` is the production implementation. It is the only class that ends
a turn or advances the current player.

### CardEffect

```java
public interface CardEffect {
    boolean canPlay(Player player, Deck deck);
    EffectOutcome play(Player player, Deck deck);
}
```

`canPlay(...)` is a pure query. Card effects do not remove cards from hands,
discard cards, or advance the current player; `TurnEngine` owns those changes.

### GamePlayViewPort

```java
public interface GamePlayViewPort {
    void render(GameSnapshot snapshot);
    void showMessage(String messageKey);
    void showFutureCards(List<CardType> cards);
    OptionalInt requestDefusePosition(int minimum, int maximum);
}
```

`GamePlayController` depends only on `TurnActions` and `GamePlayViewPort`.
Tests provide hand-written adapters for those two interfaces.

## Ownership

- Xiaoyi: contracts, `TurnEngine`, `GamePlayController`, `GamePlayView`, and
  `SkipEffect`
- Sifan: deterministic Deck support and `SeeTheFutureEffect`
- Qingyang: `Player`, `GameState`, `DiscardPile`, and
  `ExplodingKittenResolver`

## Confirmed Decisions

- [x] Confirm all method names, return types, parameters, and package placement.
- [x] Confirm that `PlayResult.rejected(PlayFailure.NONE)` is invalid.
- [x] Confirm that `GameSnapshot` requires at least one living player.
- [x] Confirm that `CardEffect.canPlay(...)` cannot change game state.
- [x] Confirm that only `TurnEngine` advances the current player.