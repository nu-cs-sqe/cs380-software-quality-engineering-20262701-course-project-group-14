package domain;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TurnEngineCatPairTests {

    @Test
    void PlayCard_WithMatchingTacocatPair_TransfersSelectedCardAndContinuesTurn() {
        Card firstCat = new Card(CardType.TACOCAT);
        Card secondCat = new Card(CardType.TACOCAT);
        Card held = new Card(CardType.SKIP);
        Card leftWithTarget = new Card(CardType.ATTACK);
        Card transferred = new Card(CardType.FAVOR);
        Player current = playerWith("Player 1", firstCat, secondCat, held);
        Player target = playerWith("Player 2", leftWithTarget, transferred);
        Card deckTop = new Card(CardType.SHUFFLE);
        Card deckBottom = new Card(CardType.NOPE);
        Deck deck = Deck.fromTopToBottom(List.of(deckTop, deckBottom));
        GameState state = new GameState(List.of(current, target), deck);
        TurnEngine engine = engineFor(state, bound -> {
            assertEquals(2, bound);
            return 1;
        });

        PlayResult result = engine.playCard(PlayCommand.pair(0, 1, 1));

        assertEquals(PlayStatus.CONTINUE_TURN, result.getStatus());
        assertEquals(PlayFailure.NONE, result.getFailure());
        assertEquals(List.of(held, transferred), current.getHand());
        assertEquals(List.of(leftWithTarget), target.getHand());
        assertEquals(2, engine.getSnapshot().getDiscardPileSize());
        assertSame(current, state.getCurrentPlayer());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
        assertEquals(2, deck.size());
        assertEquals(CardType.SHUFFLE, engine.draw().getDrawnCardType().orElseThrow());
        assertSame(deckTop, current.getHand().get(2));
        assertSame(target, state.getCurrentPlayer());
        assertSame(deckBottom, deck.drawCard());
    }

    @Test
    void PlayCard_WithPairInReverseOrder_PreservesInterveningCard() {
        Card held = new Card(CardType.SKIP);
        Card transferred = new Card(CardType.FAVOR);
        Player current = playerWith("Player 1", new Card(CardType.TACOCAT),
                held, new Card(CardType.TACOCAT));
        Player target = playerWith("Player 2", transferred);
        GameState state = new GameState(List.of(current, target),
                Deck.fromTopToBottom(List.of()));
        TurnEngine engine = engineFor(state, bound -> 0);

        PlayResult result = engine.playCard(PlayCommand.pair(2, 0, 1));

        assertEquals(PlayStatus.CONTINUE_TURN, result.getStatus());
        assertEquals(List.of(held, transferred), current.getHand());
        assertEquals(List.of(), target.getHand());
        assertEquals(2, engine.getSnapshot().getDiscardPileSize());
    }

    @Test
    void PlayCard_WithDuplicatePairIndex_RejectsWithoutChangingState() {
        Card first = new Card(CardType.TACOCAT);
        Card second = new Card(CardType.TACOCAT);
        Card gift = new Card(CardType.FAVOR);
        Card deckTop = new Card(CardType.SHUFFLE);
        Player current = playerWith("Player 1", first, second);
        Player target = playerWith("Player 2", gift);
        Deck deck = Deck.fromTopToBottom(List.of(deckTop));
        GameState state = new GameState(List.of(current, target), deck);
        TurnEngine engine = engineFor(state, bound -> {
            throw new AssertionError("An invalid pair must not choose a target card.");
        });

        PlayResult result = engine.playCard(PlayCommand.pair(0, 0, 1));

        assertEquals(PlayStatus.REJECTED, result.getStatus());
        assertEquals(PlayFailure.INVALID_SELECTION, result.getFailure());
        assertEquals(List.of(first, second), current.getHand());
        assertEquals(List.of(gift), target.getHand());
        assertEquals(0, engine.getSnapshot().getDiscardPileSize());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
        assertSame(current, state.getCurrentPlayer());
        assertEquals(1, deck.size());
        assertEquals(CardType.SHUFFLE, engine.draw().getDrawnCardType().orElseThrow());
        assertSame(deckTop, current.getHand().get(2));
        assertSame(target, state.getCurrentPlayer());
    }

    @ParameterizedTest
    @EnumSource(value = CardType.class, names = {
            "TACOCAT", "CATTERMELON", "HAIRY_POTATO_CAT", "BEARD_CAT", "RAINBOW_RALPHING_CAT"
    })
    void PlayCard_WithSupportedCatType_UsesSharedPairRule(CardType type) {
        Card held = new Card(CardType.SKIP);
        Card leftWithTarget = new Card(CardType.ATTACK);
        Card transferred = new Card(CardType.FAVOR);
        Card deckTop = new Card(CardType.SHUFFLE);
        Player current = playerWith("Player 1", new Card(type), new Card(type), held);
        Player target = playerWith("Player 2", leftWithTarget, transferred);
        Deck deck = Deck.fromTopToBottom(List.of(deckTop));
        GameState state = new GameState(List.of(current, target), deck);
        TurnEngine engine = engineFor(state, bound -> 1);

        PlayResult result = engine.playCard(PlayCommand.pair(0, 1, 1));

        assertEquals(PlayStatus.CONTINUE_TURN, result.getStatus());
        assertEquals(List.of(held, transferred), current.getHand());
        assertEquals(List.of(leftWithTarget), target.getHand());
        assertEquals(2, engine.getSnapshot().getDiscardPileSize());
        assertSame(current, state.getCurrentPlayer());
        assertEquals(1, deck.size());
        assertEquals(CardType.SHUFFLE, engine.draw().getDrawnCardType().orElseThrow());
        assertSame(deckTop, current.getHand().get(2));
        assertSame(target, state.getCurrentPlayer());
    }

    @Test
    void PlayCard_WithDifferentCatTypes_RejectsWithoutChangingState() {
        Player current = playerWith("Player 1", new Card(CardType.TACOCAT),
                new Card(CardType.BEARD_CAT));
        assertRejectionPreservesGame(rejectionGame(current, eligibleTarget()),
                engine -> engine.playCard(PlayCommand.pair(0, 1, 1)),
                PlayFailure.INVALID_SELECTION);
    }

    private static GameState rejectionGame(Player... players) {
        Deck deck = Deck.fromTopToBottom(List.of(new Card(CardType.SHUFFLE),
                new Card(CardType.NOPE)));
        return new GameState(List.of(players), deck);
    }

    private static Player matchingCatHand() {
        return playerWith("Player 1", new Card(CardType.TACOCAT), new Card(CardType.TACOCAT));
    }

    private static Player eligibleTarget() {
        return playerWith("Player 2", new Card(CardType.FAVOR));
    }

    private static void assertRejectionPreservesGame(
            GameState state, Function<TurnEngine, PlayResult> submit, PlayFailure expected
    ) {
        List<List<Card>> hands = state.getPlayers().stream()
                .map(player -> List.copyOf(player.getHand()))
                .collect(Collectors.toList());
        Player current = state.getCurrentPlayer();
        TurnEngine engine = engineFor(state, bound -> {
            throw new AssertionError("A rejected selection must not use randomness.");
        });
        PlayResult result = submit.apply(engine);

        assertEquals(PlayStatus.REJECTED, result.getStatus());
        assertEquals(expected, result.getFailure());
        assertEquals(hands, state.getPlayers().stream()
                .map(Player::getHand).collect(Collectors.toList()));
        assertEquals(0, engine.getSnapshot().getDiscardPileSize());
        assertEquals(2, engine.getSnapshot().getDeckSize());
        assertSame(current, state.getCurrentPlayer());
        assertEquals(TurnPhase.ACTIONS_ALLOWED, engine.getPhase());
        assertEquals(CardType.SHUFFLE, engine.draw().getDrawnCardType().orElseThrow());
        assertSame(state.getPlayers().get(1), state.getCurrentPlayer());
        assertEquals(CardType.NOPE, state.getDeck().drawCard().getType());
    }

    @Test
    void PlayCard_WithCatAndNonCat_RejectsWithoutChangingState() {
        Player current = playerWith("Player 1", new Card(CardType.TACOCAT),
                new Card(CardType.SKIP));
        assertRejectionPreservesGame(rejectionGame(current, eligibleTarget()),
                engine -> engine.playCard(PlayCommand.pair(0, 1, 1)),
                PlayFailure.INVALID_SELECTION);
    }

    @Test
    void PlayCard_WithFirstIndexAtHandSize_RejectsWithoutChangingState() {
        assertRejectionPreservesGame(rejectionGame(matchingCatHand(), eligibleTarget()),
                engine -> engine.playCard(PlayCommand.pair(2, 1, 1)),
                PlayFailure.INVALID_HAND_INDEX);
    }

    @Test
    void PlayCard_WithSecondIndexAtHandSize_RejectsWithoutChangingState() {
        assertRejectionPreservesGame(rejectionGame(matchingCatHand(), eligibleTarget()),
                engine -> engine.playCard(PlayCommand.pair(0, 2, 1)),
                PlayFailure.INVALID_HAND_INDEX);
    }

    @Test
    void PlayCard_WithCurrentPlayerAsTarget_RejectsWithoutChangingState() {
        assertRejectionPreservesGame(rejectionGame(matchingCatHand(), eligibleTarget()),
                engine -> engine.playCard(PlayCommand.pair(0, 1, 0)),
                PlayFailure.INVALID_TARGET);
    }

    private static Player playerWith(String name, Card... hand) {
        Player player = new Player(name);
        for (Card card : hand) {
            player.addCard(card);
        }
        return player;
    }

    private static TurnEngine engineFor(GameState state, RandomSource random) {
        DiscardPile discard = new DiscardPile();
        return new TurnEngine(state, discard, new ExplodingKittenResolver(discard),
                Map.of(), random);
    }
}
