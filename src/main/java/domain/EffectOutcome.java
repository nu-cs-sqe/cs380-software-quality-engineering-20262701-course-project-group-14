package domain;

import java.util.List;
import java.util.Objects;

public final class EffectOutcome {
    private final boolean endsTurn;
    private final List<CardType> revealedCards;

    EffectOutcome(boolean endsTurn, List<CardType> revealedCards) {
        this.endsTurn = endsTurn;
        this.revealedCards = Objects.requireNonNull(
                revealedCards,
                "Revealed cards must not be null."
        );
    }

    public boolean endsTurn() {
        return endsTurn;
    }

    public List<CardType> getRevealedCards() {
        return revealedCards;
    }
}
