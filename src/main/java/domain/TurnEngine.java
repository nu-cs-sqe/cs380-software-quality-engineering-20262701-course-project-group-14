package domain;

import java.util.Map;

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
        this.state = state;
        this.discardPile = discardPile;
        this.resolver = resolver;
        this.effects = effects;
    }

    public TurnPhase getPhase() {
        return TurnPhase.ACTIONS_ALLOWED;
    }
}
