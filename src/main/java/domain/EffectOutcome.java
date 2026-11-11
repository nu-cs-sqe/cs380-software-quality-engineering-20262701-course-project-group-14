package domain;

import java.util.List;

public final class EffectOutcome {
    private final boolean endsTurn;
    private final List<CardType> revealedCards;

    EffectOutcome(boolean endsTurn, List<CardType> revealedCards) {
        this.endsTurn = endsTurn;
        this.revealedCards = revealedCards;
    }

    public boolean endsTurn() {
        return endsTurn;
    }

    public List<CardType> getRevealedCards() {
        return revealedCards;
    }
}
