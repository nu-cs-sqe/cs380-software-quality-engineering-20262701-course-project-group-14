package domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TurnEngine implements TurnActions {
    private final GameState state;
    private final DiscardPile discardPile;
    private final ExplodingKittenResolver resolver;
    private final Map<CardType, CardEffect> effects;
    private Card pendingKitten;
    private int remainingTurns = 1;
    private boolean attackTurnsActive;

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

    @Override
    public GameSnapshot getSnapshot() {
        Player currentPlayer = state.getCurrentPlayer();
        List<CardType> hand = currentPlayer.getHand().stream()
                .map(Card::getType)
                .collect(Collectors.toList());
        long alivePlayers = state.getPlayers().stream()
                .filter(player -> !player.isEliminated())
                .count();

        return GameSnapshot.create(currentPlayer.getName(), hand, state.getDeck().size(),
                discardPile.size(), Math.toIntExact(alivePlayers), getPhase());
    }

    @Override
    public TurnPhase getPhase() {
        return pendingKitten == null
                ? TurnPhase.ACTIONS_ALLOWED
                : TurnPhase.AWAITING_DEFUSE_POSITION;
    }

    @Override
    public DefuseResult resolveDefuse(int position) {
        if (pendingKitten == null) {
            return DefuseResult.NOT_AWAITING_DEFUSE;
        }
        DefuseResult result = resolver.defuse(
                state.getCurrentPlayer(), pendingKitten, state.getDeck(), position);
        if (result == DefuseResult.DEFUSED) {
            pendingKitten = null;
            finishOneTurn();
        }
        return result;
    }

    @Override
    public DrawResult draw() {
        if (pendingKitten != null) {
            return DrawResult.rejectedPendingDefuse();
        }
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
        finishOneTurn();
        return DrawResult.normal(drawnCard.getType());
    }

    private DrawResult drawKitten(Deck deck, Card kitten) {
        ExplosionOutcome outcome = resolver.begin(state.getCurrentPlayer(), kitten);
        Card drawnKitten = deck.drawCard();
        if (outcome == ExplosionOutcome.DEFUSE_POSITION_REQUIRED) {
            pendingKitten = drawnKitten;
            return DrawResult.defuseRequired();
        }
        advanceAfterElimination();
        return DrawResult.playerEliminated();
    }

    @Override
    public PlayResult playCard(int handIndex) {
        if (pendingKitten != null) {
            return PlayResult.rejected(PlayFailure.DEFUSE_RESOLUTION_REQUIRED);
        }
        Player currentPlayer = state.getCurrentPlayer();
        int handSize = currentPlayer.getHand().size();
        if (handIndex < 0 || handIndex >= handSize) {
            return PlayResult.rejected(PlayFailure.INVALID_HAND_INDEX);
        }
        Card selectedCard = currentPlayer.getHand().get(handIndex);
        CardEffect effect = effects.get(selectedCard.getType());
        if (effect == null) {
            return PlayResult.rejected(PlayFailure.CARD_NOT_PLAYABLE);
        }
        Deck deck = state.getDeck();
        if (!effect.canPlay(currentPlayer, deck)) {
            return PlayResult.rejected(PlayFailure.PRECONDITION_NOT_MET);
        }
        Card playedCard = currentPlayer.removeCardAt(handIndex);
        EffectOutcome outcome = effect.play(currentPlayer, deck);
        discardPile.discard(playedCard);
        if (outcome.getTurnDirective()
                == TurnDirective.ATTACK_NEXT_PLAYER) {
            attackNextPlayer();
            return PlayResult.turnEnded();
        }
        if (outcome.endsTurn()) {
            finishOneTurn();
            return PlayResult.turnEnded();
        }
        return PlayResult.continueTurn(outcome.getRevealedCards());
    }

    private void finishOneTurn() {
        remainingTurns--;
        if (remainingTurns == 0) {
            state.advanceCurrentPlayer();
            remainingTurns = 1;
            attackTurnsActive = false;
        }
    }

    private void attackNextPlayer() {
        int turnsForNextPlayer = attackTurnsActive
                ? remainingTurns + 2
                : 2;
        state.advanceCurrentPlayer();
        remainingTurns = turnsForNextPlayer;
        attackTurnsActive = true;
    }

    private void advanceAfterElimination() {
        remainingTurns = 1;
        attackTurnsActive = false;
        state.advanceCurrentPlayer();
    }
}
