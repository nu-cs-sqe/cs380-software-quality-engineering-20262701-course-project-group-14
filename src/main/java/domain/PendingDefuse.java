package domain;

import java.util.Objects;

final class PendingDefuse {
    private final Card kitten;

    PendingDefuse(Card kitten) {
        this.kitten = Objects.requireNonNull(kitten, "Pending Kitten must not be null.");
    }

    public Card getKitten() {
        return kitten;
    }
}
