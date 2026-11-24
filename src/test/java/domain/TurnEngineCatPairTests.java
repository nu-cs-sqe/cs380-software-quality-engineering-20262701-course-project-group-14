package domain;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

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
