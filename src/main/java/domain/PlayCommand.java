package domain;

import java.util.OptionalInt;

public final class PlayCommand {
    private final int firstHandIndex;
    private final OptionalInt secondHandIndex;
    private final OptionalInt targetPlayerIndex;

    private PlayCommand(
            int firstHandIndex,
            OptionalInt secondHandIndex,
            OptionalInt targetPlayerIndex
    ) {
        this.firstHandIndex = firstHandIndex;
        this.secondHandIndex = secondHandIndex;
        this.targetPlayerIndex = targetPlayerIndex;
    }

    public static PlayCommand single(int handIndex) {
        requireNonNegativeHandIndex(handIndex);
        return new PlayCommand(
                handIndex,
                OptionalInt.empty(),
                OptionalInt.empty()
        );
    }

    public static PlayCommand targeted(
            int handIndex,
            int targetPlayerIndex
    ) {
        requireNonNegativeHandIndex(handIndex);
        return new PlayCommand(
                handIndex,
                OptionalInt.empty(),
                OptionalInt.of(targetPlayerIndex)
        );
    }

    public int getFirstHandIndex() {
        return firstHandIndex;
    }

    public OptionalInt getSecondHandIndex() {
        return secondHandIndex;
    }

    public OptionalInt getTargetPlayerIndex() {
        return targetPlayerIndex;
    }

    private static void requireNonNegativeHandIndex(int handIndex) {
        if (handIndex < 0) {
            throw new IllegalArgumentException(
                    "Hand index must not be negative."
            );
        }
    }
}
