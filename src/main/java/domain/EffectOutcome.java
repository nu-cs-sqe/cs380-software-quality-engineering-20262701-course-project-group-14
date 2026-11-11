package domain;

import java.util.List;
import java.util.Objects;

public final class EffectOutcome {
    private final boolean endsTurn;
    private final List<CardType> revealedCards;

    EffectOutcome(boolean endsTurn, List<CardType> revealedCards) {
        this.endsTurn = endsTurn;
        List<CardType> checkedCards = Objects.requireNonNull(
                revealedCards,
                "Revealed cards must not be null."
        );

        for (CardType card : checkedCards) {
            if (card == null) {
                throw new NullPointerException(
                        "Revealed cards must not contain null."
                );
            }
        }

        this.revealedCards = List.copyOf(checkedCards);
    }

    public boolean endsTurn() {
        return endsTurn;
    }

    public List<CardType> getRevealedCards() {
        return revealedCards;
    }
}
