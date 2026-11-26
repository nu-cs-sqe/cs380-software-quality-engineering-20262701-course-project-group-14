package domain;

import java.util.Objects;

final class PendingDefuse {
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
}
