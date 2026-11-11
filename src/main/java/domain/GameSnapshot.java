package domain;

import java.util.List;
import java.util.Objects;

public final class GameSnapshot {
    private final String currentPlayerName;
    private final List<CardType> currentPlayerHand;
    private final int deckSize;
    private final int discardPileSize;
    private final int alivePlayerCount;
    private final TurnPhase phase;

    private GameSnapshot(
            String currentPlayerName,
            List<CardType> currentPlayerHand,
            int deckSize,
            int discardPileSize,
            int alivePlayerCount,
            TurnPhase phase
    ) {
        this.currentPlayerName = currentPlayerName;
        this.currentPlayerHand = currentPlayerHand;
        this.deckSize = deckSize;
        this.discardPileSize = discardPileSize;
        this.alivePlayerCount = alivePlayerCount;
        this.phase = phase;
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
}
