package domain;

import java.util.List;

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
        return new GameSnapshot(
                currentPlayerName,
                currentPlayerHand,
                deckSize,
                discardPileSize,
                alivePlayerCount,
                phase
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
