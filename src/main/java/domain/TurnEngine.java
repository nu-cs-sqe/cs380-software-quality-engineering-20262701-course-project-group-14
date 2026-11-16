package domain;

import java.util.Map;
import java.util.Objects;

public final class TurnEngine {
    private final GameState state;
    private final DiscardPile discardPile;
    private final ExplodingKittenResolver resolver;
    private final Map<CardType, CardEffect> effects;
    private Card pendingKitten;

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

        this.effects = Map.copyOf(checkedEffects);
    }

    public TurnPhase getPhase() {
        return pendingKitten == null
                ? TurnPhase.ACTIONS_ALLOWED
                : TurnPhase.AWAITING_DEFUSE_POSITION;
    }

    public DefuseResult resolveDefuse(int position) {
        return DefuseResult.NOT_AWAITING_DEFUSE;
    }

    public DrawResult draw() {
        Deck deck = state.getDeck();
        if (deck.size() == 0) {
            return DrawResult.emptyDeck();
        }
        Card topCard = deck.peekTopCard();
        if (topCard.getType() == CardType.EXPLODING_KITTEN) {
            return drawKitten(deck, topCard);
        }

        Card drawnCard = deck.drawCard();
        state.getCurrentPlayer().addCard(drawnCard);
        state.advanceCurrentPlayer();
        return DrawResult.normal(drawnCard.getType());
    }

    private DrawResult drawKitten(Deck deck, Card kitten) {
        ExplosionOutcome outcome = resolver.begin(state.getCurrentPlayer(), kitten);
        if (outcome != ExplosionOutcome.DEFUSE_POSITION_REQUIRED) {
            throw new UnsupportedOperationException(
                    "Player elimination is not implemented."
            );
        }
        pendingKitten = deck.drawCard();
        return DrawResult.defuseRequired();
    }

    public PlayResult playCard(int handIndex) {
        int handSize = state.getCurrentPlayer().getHand().size();
        if (handIndex < 0 || handIndex >= handSize) {
            return PlayResult.rejected(PlayFailure.INVALID_HAND_INDEX);
        }
        Card selectedCard = state.getCurrentPlayer()
                .getHand()
                .get(handIndex);
        CardEffect effect = effects.get(selectedCard.getType());
        if (effect == null) {
            return PlayResult.rejected(PlayFailure.CARD_NOT_PLAYABLE);
        }
        Player currentPlayer = state.getCurrentPlayer();
        if (!effect.canPlay(currentPlayer, state.getDeck())) {
            return PlayResult.rejected(PlayFailure.PRECONDITION_NOT_MET);
        }
        throw new UnsupportedOperationException(
                "Card play is not implemented."
        );
    }
}
