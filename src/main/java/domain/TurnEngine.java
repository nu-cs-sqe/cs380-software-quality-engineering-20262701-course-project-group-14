package domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TurnEngine implements TurnActions, CommandPlayActions {
    private final GameState state;
    private final DiscardPile discardPile;
    private final ExplodingKittenResolver resolver;
    private final Map<CardType, CardEffect> effects;
    private final RandomSource random;
    private Card pendingKitten;
    private int remainingTurns = 1;
    private boolean attackTurnsActive;

    public TurnEngine(
            GameState state,
            DiscardPile discardPile,
            ExplodingKittenResolver resolver,
            Map<CardType, CardEffect> effects
    ) {
        this(state, discardPile, resolver, effects, new Random()::nextInt);
    }

    TurnEngine(
            GameState state,
            DiscardPile discardPile,
            ExplodingKittenResolver resolver,
            Map<CardType, CardEffect> effects,
            RandomSource random
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
        this.random = Objects.requireNonNull(random, "Random source must not be null.");
    }

    @Override
    public GameSnapshot getSnapshot() {
        Player currentPlayer = state.getCurrentPlayer();
        List<CardType> hand = currentPlayer.getHand().stream()
                .map(Card::getType)
                .collect(Collectors.toList());
        List<Player> players = state.getPlayers();
        List<PlayerSummary> summaries = IntStream.range(0, players.size())
                .mapToObj(index -> playerSummary(index, players.get(index)))
                .collect(Collectors.toList());
        long alivePlayers = summaries.stream()
                .filter(summary -> !summary.isEliminated())
                .count();

        return GameSnapshot.create(currentPlayer.getName(), hand, state.getDeck().size(),
                discardPile.size(), Math.toIntExact(alivePlayers), getPhase())
                .withPlayerSummaries(summaries, players.indexOf(currentPlayer));
    }

    private static PlayerSummary playerSummary(int seat, Player player) {
        return PlayerSummary.create(seat, player.getName(), player.getHand().size(),
                player.isEliminated());
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
        if (handIndex < 0) {
            PlayFailure failure = pendingKitten != null
                    ? PlayFailure.DEFUSE_RESOLUTION_REQUIRED
                    : PlayFailure.INVALID_HAND_INDEX;
            return PlayResult.rejected(failure);
        }
        return playCard(PlayCommand.single(handIndex));
    }

    private PlayResult playRegisteredEffect(Player currentPlayer, int handIndex) {
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

    @Override
    public PlayResult playCard(PlayCommand command) {
        Objects.requireNonNull(command, "Play command must not be null.");
        if (pendingKitten != null) {
            return PlayResult.rejected(PlayFailure.DEFUSE_RESOLUTION_REQUIRED);
        }
        if (command.getSecondHandIndex().isEmpty()) {
            return playSingleCommand(command);
        }
        return playCatPair(command);
    }

    private PlayResult playSingleCommand(PlayCommand command) {
        int handIndex = command.getFirstHandIndex();
        Player player = state.getCurrentPlayer();
        List<Card> hand = player.getHand();
        if (handIndex >= hand.size()) {
            return PlayResult.rejected(PlayFailure.INVALID_HAND_INDEX);
        }
        if (hand.get(handIndex).getType().isCatCard()
                || command.getTargetPlayerIndex().isPresent()) {
            return PlayResult.rejected(PlayFailure.INVALID_SELECTION);
        }
        return playRegisteredEffect(player, handIndex);
    }

    private PlayResult playCatPair(PlayCommand command) {
        Player player = state.getCurrentPlayer();
        int first = command.getFirstHandIndex();
        int second = command.getSecondHandIndex().getAsInt();
        List<Card> hand = player.getHand();
        if (first >= hand.size() || second >= hand.size()) {
            return PlayResult.rejected(PlayFailure.INVALID_HAND_INDEX);
        }
        Card firstCard = hand.get(first);
        Card secondCard = hand.get(second);
        if (first == second || !isMatchingCatPair(firstCard, secondCard)) {
            return PlayResult.rejected(PlayFailure.INVALID_SELECTION);
        }
        return transferCatPair(player, command.getTargetPlayerIndex().getAsInt(),
                first, second);
    }

    private static boolean isMatchingCatPair(Card first, Card second) {
        return first.getType().isCatCard() && first.getType() == second.getType();
    }

    private PlayResult transferCatPair(Player player, int seat, int first, int second) {
        List<Player> players = state.getPlayers();
        if (seat >= players.size()) {
            return PlayResult.rejected(PlayFailure.INVALID_TARGET);
        }
        Player target = players.get(seat);
        if (target == player || target.isEliminated()) {
            return PlayResult.rejected(PlayFailure.INVALID_TARGET);
        }
        int targetHandSize = target.getHand().size();
        if (targetHandSize == 0) {
            return PlayResult.rejected(PlayFailure.TARGET_HAND_EMPTY);
        }
        int stolenIndex = random.nextInt(targetHandSize);
        Card stolen = target.removeCardAt(stolenIndex);
        Card higher = player.removeCardAt(Math.max(first, second));
        Card lower = player.removeCardAt(Math.min(first, second));
        discardPile.discardAll(List.of(lower, higher));
        player.addCard(stolen);
        return PlayResult.continueTurn(List.of());
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
