package domain;

import java.util.Objects;

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
        String checkedName = Objects.requireNonNull(
                name,
                "Player name must not be null."
        );
        if (checkedName.isBlank()) {
            throw new IllegalArgumentException(
                    "Player name must not be blank."
            );
        }
        return new PlayerSummary(
                playerIndex,
                checkedName,
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
