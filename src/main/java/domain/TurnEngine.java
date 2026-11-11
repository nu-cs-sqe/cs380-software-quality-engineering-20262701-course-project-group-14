package domain;

import java.util.Map;
import java.util.Objects;

public final class TurnEngine {
    private final GameState state;
    private final DiscardPile discardPile;
    private final ExplodingKittenResolver resolver;
    private final Map<CardType, CardEffect> effects;

    public TurnEngine(
            GameState state,
            DiscardPile discardPile,
            ExplodingKittenResolver resolver,
            Map<CardType, CardEffect> effects
    ) {
        this.state = Objects.requireNonNull(
                state,
                "Game state must not be null."
        );
        this.discardPile = Objects.requireNonNull(
                discardPile,
                "Discard pile must not be null."
        );
        this.resolver = Objects.requireNonNull(
                resolver,
                "Exploding Kitten resolver must not be null."
        );
        Map<CardType, CardEffect> checkedEffects = Objects.requireNonNull(
                effects,
                "Card effects must not be null."
        );

        for (CardType type : checkedEffects.keySet()) {
            Objects.requireNonNull(
                    type,
                    "Card effect type must not be null."
            );
        }

        for (CardEffect effect : checkedEffects.values()) {
            Objects.requireNonNull(
                    effect,
                    "Card effect must not be null."
            );
        }

        this.effects = checkedEffects;
    }

    public TurnPhase getPhase() {
        return TurnPhase.ACTIONS_ALLOWED;
    }

    public DefuseResult resolveDefuse(int position) {
        return DefuseResult.NOT_AWAITING_DEFUSE;
    }

    public DrawResult draw() {
        if (state.getDeck().size() == 0) {
            return DrawResult.emptyDeck();
        }
        throw new UnsupportedOperationException(
                "Non-empty draw is not implemented."
        );
    }

    public PlayResult playCard(int handIndex) {
        int handSize = state.getCurrentPlayer().getHand().size();
        if (handIndex < 0 || handIndex >= handSize) {
            return PlayResult.rejected(PlayFailure.INVALID_HAND_INDEX);
        }
        throw new UnsupportedOperationException(
                "Card play is not implemented."
        );
    }
}
