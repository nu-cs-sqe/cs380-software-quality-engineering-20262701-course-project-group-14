package domain;

import java.util.List;
import java.util.Objects;

public final class EffectOutcome {
    private final TurnDirective turnDirective;
    private final List<CardType> revealedCards;

    EffectOutcome(boolean endsTurn, List<CardType> revealedCards) {
        this(
                endsTurn
                        ? TurnDirective.END_TURN
                        : TurnDirective.CONTINUE,
                revealedCards
        );
    }

    EffectOutcome(
            TurnDirective turnDirective,
            List<CardType> revealedCards
    ) {
        this.turnDirective = Objects.requireNonNull(
                turnDirective,
                "Turn directive must not be null."
        );
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
        return turnDirective != TurnDirective.CONTINUE;
    }

    public TurnDirective getTurnDirective() {
        return turnDirective;
    }

    public List<CardType> getRevealedCards() {
        return revealedCards;
    }
}