package domain;

final class PendingDefuse {
    private final Card kitten;

    PendingDefuse(Card kitten) {
        this.kitten = kitten;
    }

    public Card getKitten() {
        return kitten;
    }
}
