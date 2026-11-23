package domain;

public final class PlayerSummary {
    private final int playerIndex;
    private final String name;
    private final int handSize;
    private final boolean eliminated;

    private PlayerSummary(
            int playerIndex,
            String name,
            int handSize,
            boolean eliminated
    ) {
        this.playerIndex = playerIndex;
        this.name = name;
        this.handSize = handSize;
        this.eliminated = eliminated;
    }

    public static PlayerSummary create(
            int playerIndex,
            String name,
            int handSize,
            boolean eliminated
    ) {
        if (playerIndex < 0) {
            throw new IllegalArgumentException(
                    "Player index must not be negative."
            );
        }
        return new PlayerSummary(
                playerIndex,
                name,
                handSize,
                eliminated
        );
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public String getName() {
        return name;
    }

    public int getHandSize() {
        return handSize;
    }

    public boolean isEliminated() {
        return eliminated;
    }
}
