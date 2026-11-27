package domain;

import java.util.Objects;

final class PendingDefuse implements PendingAction {
    private final Card kitten;

    PendingDefuse(Card kitten) {
        Objects.requireNonNull(kitten, "Pending Kitten must not be null.");
        if (kitten.getType() != CardType.EXPLODING_KITTEN) {
            throw new IllegalArgumentException(
                    "Pending Defuse requires an Exploding Kitten.");
        }
        this.kitten = kitten;
    }

    public Card getKitten() {
        return kitten;
    }

    @Override
    public TurnPhase getPhase() {
        return TurnPhase.AWAITING_DEFUSE_POSITION;
    }
}
