package domain;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TurnEngineFactoryTests {

    @Test
    public void Create_WithNullState_ThrowsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> TurnEngineFactory.create(null)
        );

        assertEquals(
                "Game state must not be null.",
                exception.getMessage()
        );
    }

    @Test
    public void Create_WithAttack_PassesTurnAfterTwoDraws() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        first.addCard(new Card(CardType.ATTACK));
        Deck deck = emptyDeck();
        Card skip = new Card(CardType.SKIP);
        Card favor = new Card(CardType.FAVOR);
        deck.addCards(List.of(skip, favor));
        GameState state = new GameState(List.of(first, second), deck);
        TurnActions actions = TurnEngineFactory.create(state);

        PlayResult playResult = actions.playCard(0);

        assertEquals(PlayStatus.TURN_ENDED, playResult.getStatus());
        assertEquals(0, first.getHand().size());
        assertSame(second, state.getCurrentPlayer());
        assertEquals(1, actions.getSnapshot().getDiscardPileSize());
        assertEquals(2, actions.getSnapshot().getRemainingTurns());

        DrawResult firstDraw = actions.draw();

        assertEquals(DrawStatus.NORMAL_CARD_DRAWN, firstDraw.getStatus());
        assertSame(second, state.getCurrentPlayer());
        assertEquals(1, actions.getSnapshot().getRemainingTurns());

        DrawResult secondDraw = actions.draw();

        assertEquals(DrawStatus.NORMAL_CARD_DRAWN, secondDraw.getStatus());
        assertSame(first, state.getCurrentPlayer());
        assertEquals(1, actions.getSnapshot().getRemainingTurns());
        assertEquals(List.of(skip, favor), second.getHand());
    }

    @Test
    public void Create_WithSkip_RegistersEffectAndEndsTurn() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        first.addCard(new Card(CardType.SKIP));
        GameState state = new GameState(
                List.of(first, second),
                emptyDeck()
        );
        TurnActions actions = TurnEngineFactory.create(state);

        PlayResult result = actions.playCard(0);

        assertEquals(PlayStatus.TURN_ENDED, result.getStatus());
        assertEquals(0, first.getHand().size());
        assertSame(second, state.getCurrentPlayer());
        assertEquals(1, actions.getSnapshot().getDiscardPileSize());
    }

    @Test
    public void Create_WithSniffTest_MovesKittenToBottomAndKeepsTurn() {
        Player first = new Player("Player 1");
        Player second = new Player("Player 2");
        first.addCard(new Card(CardType.SNIFF_TEST));
        Card kitten = new Card(CardType.EXPLODING_KITTEN);
        Card skip = new Card(CardType.SKIP);
        Deck deck = Deck.fromTopToBottom(List.of(kitten, skip));
        GameState state = new GameState(List.of(first, second), deck);
        TurnActions actions = TurnEngineFactory.create(state);

        PlayResult playResult = actions.playCard(0);

        assertEquals(PlayStatus.CONTINUE_TURN, playResult.getStatus());
        assertEquals(List.of(CardType.EXPLODING_KITTEN), playResult.getRevealedCards());
        assertEquals(0, first.getHand().size());
        assertEquals(1, actions.getSnapshot().getDiscardPileSize());
        assertSame(first, state.getCurrentPlayer());

        DrawResult draw = actions.draw();

        assertEquals(DrawStatus.NORMAL_CARD_DRAWN, draw.getStatus());
        assertEquals(List.of(skip), first.getHand());
        assertSame(second, state.getCurrentPlayer());
        assertEquals(1, deck.size());
        assertSame(kitten, deck.drawCard());
    }

    @Test
    public void Create_WithRandomAndNullState_RejectsState() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> TurnEngineFactory.create(null, bound -> 0));
        assertEquals("Game state must not be null.", exception.getMessage());
    }

    @Test
    public void Create_WithNullRandom_RejectsRandom() {
        GameState state = new GameState(List.of(new Player("Player 1")), emptyDeck());
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> TurnEngineFactory.create(state, null));
        assertEquals("Random source must not be null.", exception.getMessage());
    }

    private static Deck emptyDeck() {
        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            deck.removeCards(type, deck.countOf(type));
        }
        return deck;
    }
}
