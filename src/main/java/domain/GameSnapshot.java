package domain;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

public final class GameSnapshot {
    private final String currentPlayerName;
    private final List<CardType> currentPlayerHand;
    private final int deckSize;
    private final int discardPileSize;
    private final int alivePlayerCount;
    private final TurnPhase phase;
    private final List<PlayerSummary> players;
    private final int currentPlayerIndex;
    private final int remainingTurns;

    private GameSnapshot(
            String currentPlayerName,
            List<CardType> currentPlayerHand,
            int deckSize,
            int discardPileSize,
            int alivePlayerCount,
            TurnPhase phase
    ) {
        this.currentPlayerName = currentPlayerName;
        this.currentPlayerHand = List.copyOf(currentPlayerHand);
        this.deckSize = deckSize;
        this.discardPileSize = discardPileSize;
        this.alivePlayerCount = alivePlayerCount;
        this.phase = phase;
        this.players = List.of();
        this.currentPlayerIndex = 0;
        this.remainingTurns = phase == TurnPhase.GAME_OVER ? 0 : 1;
    }

    private GameSnapshot(GameSnapshot base, List<PlayerSummary> players,
                         int currentPlayerIndex, int remainingTurns) {
        this.currentPlayerName = base.currentPlayerName;
        this.currentPlayerHand = base.currentPlayerHand;
        this.deckSize = base.deckSize;
        this.discardPileSize = base.discardPileSize;
        this.alivePlayerCount = base.alivePlayerCount;
        this.phase = base.phase;
        this.players = List.copyOf(players);
        this.currentPlayerIndex = currentPlayerIndex;
        this.remainingTurns = remainingTurns;
    }

    GameSnapshot withTurnDetails(List<PlayerSummary> players, int currentPlayerIndex,
                                 int remainingTurns) {
        return new GameSnapshot(this, players, currentPlayerIndex, remainingTurns);
    }

    public static GameSnapshot create(
            String currentPlayerName,
            List<CardType> currentPlayerHand,
            int deckSize,
            int discardPileSize,
            int alivePlayerCount,
            TurnPhase phase
    ) {
        String checkedName = Objects.requireNonNull(
                currentPlayerName,
                "Current player name must not be null."
        );
        List<CardType> checkedHand = Objects.requireNonNull(
                currentPlayerHand,
                "Current player hand must not be null."
        );

        for (CardType card : checkedHand) {
            if (card == null) {
                throw new NullPointerException(
                        "Current player hand must not contain null."
                );
            }
        }

        if (deckSize < 0) {
            throw new IllegalArgumentException(
                    "Deck size must not be negative."
            );
        }

        if (discardPileSize < 0) {
            throw new IllegalArgumentException(
                    "Discard pile size must not be negative."
            );
        }

        if (alivePlayerCount < 1) {
            throw new IllegalArgumentException(
                    "Alive player count must be at least 1."
            );
        }

        return new GameSnapshot(
                checkedName,
                checkedHand,
                deckSize,
                discardPileSize,
                alivePlayerCount,
                Objects.requireNonNull(
                        phase,
                        "Turn phase must not be null."
                )
        );
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public List<CardType> getCurrentPlayerHand() {
        return currentPlayerHand;
    }

    public int getDeckSize() {
        return deckSize;
    }

    public int getDiscardPileSize() {
        return discardPileSize;
    }

    public int getAlivePlayerCount() {
        return alivePlayerCount;
    }

    public TurnPhase getPhase() {
        return phase;
    }

    public List<PlayerSummary> getPlayers() {
        return players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public OptionalInt getWinnerIndex() {
        return phase == TurnPhase.GAME_OVER && !players.isEmpty()
                ? OptionalInt.of(currentPlayerIndex) : OptionalInt.empty();
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }
}
